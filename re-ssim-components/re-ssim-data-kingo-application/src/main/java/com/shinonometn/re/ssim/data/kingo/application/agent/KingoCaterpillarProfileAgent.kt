package com.shinonometn.re.ssim.data.kingo.application.agent

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.data.kingo.application.base.CaptureTaskStage
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.capture.CourseDetailsPageProcessor
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.capture.CoursesListPageProcessor
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.capture.LoginExecutePageProcessor
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.capture.LoginPreparePageProcessor
import com.shinonometn.re.ssim.data.kingo.application.pojo.CourseLabelItem
import com.shinonometn.re.ssim.data.kingo.application.pojo.TermLabelItem
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.TermListPageProcessor
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.model.HttpRequestBody
import us.codecraft.webmagic.utils.HttpConstant
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class KingoCaterpillarProfileAgent(private val map: MutableMap<String, Any?>) : CaterpillarProfileAgent(map) {

    private val logger = LoggerFactory.getLogger("caterpillar_profile_agent.${this.requireAgentCode()}")

    var username: String? by map.withDefault { null }
    var password: String? by map.withDefault { null }
    var role: String by map.withDefault { "STU" }

    var encoding: String by map.withDefault { "utf8" }

    @JsonIgnore
    override fun requireTargetDomain(): String = "jwgl.lnc.edu.cn"

    override fun requireAgentCode() = "kingo.lingnan_college"

    override fun createSite(): Site = super.createSite().setCharset(encoding)

    override fun fetchTerms(): Collection<TermLabelItem> {

        val result = LinkedList<TermLabelItem>()

        Spider.create(TermListPageProcessor(createSite()))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline { r, _ ->
                    result.addAll(
                            TermListPageProcessor
                                    .getTerms(r)
                                    .entries
                                    .stream()
                                    .map { TermLabelItem(it.key, it.value) }
                                    .collect(Collectors.toList())
                    )
                }
                .run()

        return result;
    }

    override fun fetchCoursesData(taskUUID: String,
                                  termCode: String,
                                  storageFolder: File): Flux<ProfileAgentMessage> = Flux.create {

        if (!storageFolder.exists() && !storageFolder.mkdirs()) throw Exception("Could not create work directory for task.")
        if (!storageFolder.isDirectory) throw Exception("Given file ${storageFolder.absolutePath} is not a directory.")

        val site = createSite()

        it.next(ProfileAgentMessage(CaptureTaskStage.INITIALIZE, "preparing_login_to_kingo"))
        val prepareContextData = prepareLoginToKingo(site)

        it.next(ProfileAgentMessage(CaptureTaskStage.INITIALIZE, "login_to_kingo"))
        doLoginToKingo(site, prepareContextData)

        it.next(ProfileAgentMessage(CaptureTaskStage.INITIALIZE, "fetching_remote_courses_list"))
        val courseCodeList = fetchTermCourseList(site, termCode)
                .stream()
                .map { item -> createSubjectRequest(site, termCode, item.courseCode!!) }
                .collect(Collectors.toList())

        it.next(ProfileAgentMessage(CaptureTaskStage.INITIALIZE, "preparing_caterpillar"))
        val spider = Spider.create(CourseDetailsPageProcessor(site))
                .startRequest(courseCodeList)
                .addPipeline { resultItems, _ ->
                    try {
                        val course = CourseDetailsPageProcessor.getSubject(resultItems)
                        JSON.write(FileOutputStream(File(storageFolder, "${Objects.requireNonNull(course.code)}.json")), course)
                        logger.debug("downloaded: [${course.code}] ${course.name}")
                    } catch (e: Exception) {
                        it.error(Exception("Spider $taskUUID failed: ${e.message}", e))
                    }
                }
                .setUUID(taskUUID)
                .thread(taskThreads!!)

        registerSpiderToMonitor(spider)

        it.next(ProfileAgentMessage(CaptureTaskStage.CAPTURE, "downloading_courses"))
        try {
            spider.run()
            it.next(ProfileAgentMessage(CaptureTaskStage.STOPPED, "terminated"))
        } catch (e: Exception) {
            it.error(e)
        }
    }

    override fun validateSetting() {
        val site = createSite()
        val contextData = prepareLoginToKingo(site)
        doLoginToKingo(site, contextData)
    }

    /*
    * Prepare login to kingo
    */
    private fun prepareLoginToKingo(site: Site): Map<String, Any> {
        val prepareContextData = HashMap<String, Any>()
        Spider.create(LoginPreparePageProcessor(username, password, role, site))
                .addUrl(KingoUrls.loginPageAddress)
                .addPipeline { r, _ -> prepareContextData.putAll(r.all) }
                .run()

        if (!LoginPreparePageProcessor.getIsReady(prepareContextData)) throw IllegalStateException("could_not_get_login_form")

        return prepareContextData;
    }

    /**
     * login kingo site
     */
    private fun doLoginToKingo(site: Site, prepareContextData: Map<String, Any>) {

        val loginResult = AtomicBoolean(false)

        val cookies = LoginPreparePageProcessor.getCookie(prepareContextData)
        if (cookies != null) site.addCookie("ASP.NET_SessionId", cookies["ASP.NET_SessionId"])

        val formFields = LoginPreparePageProcessor.getFormFields(prepareContextData)

        val loginRequest = Request(KingoUrls.loginPageAddress)
        loginRequest.method = HttpConstant.Method.POST
        loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded")
        loginRequest.addHeader("Referer", KingoUrls.loginPageAddress)
        loginRequest.requestBody = HttpRequestBody.form(formFields, Objects.requireNonNull(encoding))


        Spider.create(LoginExecutePageProcessor(site))
                .addRequest(loginRequest)
                .addPipeline { resultItems, _ -> loginResult.set(LoginExecutePageProcessor.getIsLogin(resultItems)!!) }
                .run()

        logger.debug("Login to kingo {}.", if (loginResult.get()) "successful" else "failed")
        if (!loginResult.get()) throw BusinessException("login_to_kingo_failed")
    }

    /**
     * Fetch course list
     */
    private fun fetchTermCourseList(site: Site, termCode: String?): Collection<CourseLabelItem> {
        val termList = LinkedList<CourseLabelItem>()

        Spider.create(CoursesListPageProcessor(site))
                .addUrl(KingoUrls.subjectListQueryPath + termCode!!)
                .addPipeline { resultItems, _ ->
                    termList.addAll(
                            CoursesListPageProcessor
                                    .getCourseList(resultItems)
                                    .entries
                                    .stream()
                                    .map { CourseLabelItem(it.key, it.value) }
                                    .collect(Collectors.toList())
                    )
                }
                .run()

        logger.debug("Fetched remote course list of term {}.", termCode)

        return termList
    }

    /**
     * Create request model for each course
     */
    private fun createSubjectRequest(site: Site, termCode: String, subjectCode: String): Request {
        val form = HashMap<String, Any>()
        form["gs"] = "2"
        form["txt_yzm"] = ""
        form["Sel_XNXQ"] = termCode
        form["Sel_KC"] = subjectCode

        val request = Request(KingoUrls.subjectQueryPage)
        request.method = HttpConstant.Method.POST
        request.requestBody = HttpRequestBody.form(form, site.charset)
        request.addHeader("Referer", KingoUrls.classInfoQueryPage)

        return request

    }
}