package com.shinonometn.re.ssim.caterpillar.application.service

import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTaskDetails
import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.caterpillar.application.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.*
import org.slf4j.LoggerFactory
import org.springframework.core.task.TaskExecutor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
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
import java.util.stream.Stream

@Service
class CaterpillarService(private val fileManageService: CaterpillarFileManageService,
                         private val spiderMonitor: SpiderMonitor,
                         private val taskExecutor: TaskExecutor,
                         private val captureTaskRepository: CaptureTaskRepository) {

    private val logger = LoggerFactory.getLogger(CaterpillarService::class.java)

    /*

        Status

    */

    //    /**
    //     * Get importing task count
    //     *
    //     * @return int
    //     */
    //    public Integer getImportingTaskCount() {
    //        return caterpillarMonitorStore.getImportTaskCount();
    //    }

    /**
     * Get running spider counts
     *
     * @return long
     */
    fun getCapturingTaskCount(): Long =
            spiderMonitor.getSpiderStatus()
                    .values
                    .stream()
                    .filter { i -> i.status == "Running" }
                    .count()


    /**
     * Get all school terms
     *
     *
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    fun captureTermListFromRemote(): Map<String, String> {

        val capturedResult = HashMap<String, String>()

        Spider.create(TermListPageProcessor(CaterpillarSetting.createDefaultSite()))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline { r, _ -> capturedResult.putAll(TermListPageProcessor.getTerms(r)) }
                .run()

        return capturedResult
    }

    /**
     * Get all tasks
     *
     * @return task list
     */
    fun listAllTasks(pageable: Pageable): Page<CaptureTaskDetails> {
        return captureTaskRepository.findAll(pageable).map { this.getTaskDetails(it) }
    }

    /*
     *
     *
     * Task management
     *
     *
     *
     *
     * */

    /**
     * Create a termName capture task with code
     *
     * @param termCode termName code
     * @return created task
     */
    fun createByTermCode(termCode: String): CaptureTask {
        val captureTask = CaptureTask()

        captureTask.createDate = Date()
        captureTask.termCode = termCode
        captureTask.termName = captureTermListFromRemote()[termCode]

        captureTask.stage = CaptureTaskStage.NONE
        captureTask.stageReport = "task_created"

        return captureTaskRepository.save(captureTask)
    }

    /**
     * Stop a task
     *
     * @param taskId task id
     * @return task dto
     */
    fun stopTaskById(taskId: String): CaptureTaskDetails? {
        val captureTask = captureTaskRepository.findById(taskId).orElse(null) ?: return null

        val spiderStatus = spiderMonitor.getSpiderStatus()[taskId]
                ?: throw BusinessException("task_have_not_initialized")
        spiderStatus.stop()

        changeCaptureTaskStatus(captureTask, null, "task_has_been_stopped")

        return captureTaskRepository
                .findById(taskId)
                .map { this.getTaskDetails(it) }
                .orElse(null)
    }

    /**
     * Resume a stopped task
     *
     * @param taskId task id
     * @return dto
     */
    fun resumeTaskById(taskId: String): CaptureTaskDetails? {
        val captureTaskDetails = captureTaskRepository
                .findById(taskId)
                .map { this.getTaskDetails(it) }
                .orElse(null)
                ?: return null

        val spiderStatus = captureTaskDetails.runningTaskStatus ?: throw BusinessException("task_have_not_initialized")
        if (spiderStatus.name == Spider.Status.Running.name) throw BusinessException("spider_running")

        spiderStatus.start()
        changeCaptureTaskStatus(captureTaskDetails.taskInfo, null, "task_resumed")

        return captureTaskDetails
    }

    /**
     * Start a capture task by task id
     *
     *
     * It will capture all subject info to a temporal folder
     *
     * @param taskId task id
     * @return dto
     */
    fun startByTaskIdAndSettings(taskId: String, caterpillarSetting: CaterpillarSetting): CaptureTaskDetails {

        val captureTaskDetails = captureTaskRepository
                .findById(taskId)
                .map<CaptureTaskDetails>(Function<CaptureTask, CaptureTaskDetails> { this.getTaskDetails(it) })
                .orElseThrow { BusinessException("task_not_exists") }

        if (captureTaskDetails.runningTaskStatus != null) throw BusinessException("task_thread_exists")

        val captureTask = captureTaskDetails.taskInfo

        changeCaptureTaskStatus(captureTask, CaptureTaskStage.INITIALIZE, "task_initialing")
        emitTaskCreateMessage()

        taskExecutor.execute {

            changeCaptureTaskStatus(captureTask, null, "login_to_kingo")

            try {
                val site = doLogin(caterpillarSetting)

                val dataFolder = fileManageService.contextOf(taskId)
                if (!dataFolder.exists() && !dataFolder.file.mkdirs())
                    throw BusinessException("Could not create work directory for task")

                val spider = Spider.create(CourseDetailsPageProcessor(site))
                        .addPipeline { resultItems, task ->
                            try {
                                val course = CourseDetailsPageProcessor.getSubject(resultItems)
                                JSON.write(FileOutputStream(File(dataFolder.file, Objects.requireNonNull(course.code))), course)
                            } catch (e: Exception) {
                                changeCaptureTaskStatus(captureTask, null, "failed:" + e.message)
                                throw RuntimeException(e)
                            }
                        }
                        .setUUID(taskId)
                        .thread(caterpillarSetting.threads)

                spider.startRequest(fetchTermCourseList(site, captureTaskDetails.taskInfo.termCode)
                        .stream()
                        .map { id -> createSubjectRequest(site, captureTaskDetails.taskInfo.termCode, id) }
                        .collect<List<Request>, Any>(Collectors.toList()))

                spiderMonitor.register(spider)

                changeCaptureTaskStatus(captureTask, CaptureTaskStage.CAPTURE, "downloading")
                spider.run()
                changeCaptureTaskStatus(captureTask, CaptureTaskStage.STOPPED, "stopped")

            } catch (e: BusinessException) {
                changeCaptureTaskStatus(captureTask, null, "failed:" + e.message)
            } finally {
                emitTaskFinishMessage()
            }
        }

        return captureTaskDetails
    }

    private fun emitTaskCreateMessage() {
        // TODO
    }

    private fun emitTaskFinishMessage() {
        // TODO
    }


    /**
     * Delete a not running task
     *
     * @param id task id
     */
    fun delete(id: String) {
        val spiderStatusMap = spiderMonitor.getSpiderStatus()

        if (spiderStatusMap.containsKey(id)) {

            val spiderStatus = spiderStatusMap[id]
            if (spiderStatus.status == Spider.Status.Running.name)
                throw BusinessException("spider_running")

            spiderStatusMap.remove(id)
        }

        captureTaskRepository.deleteById(id)
    }

    /**
     * Check if caterpillar setting valid
     *
     * @param caterpillarSetting settings
     * @return result
     */
    fun validateSettings(caterpillarSetting: CaterpillarSetting): Boolean {
        return doLogin(caterpillarSetting) != null
    }

    /**
     * Get a task dto by id
     *
     * @param id id
     * @return dto
     */
    fun queryTask(id: String): CaptureTaskDetails? {
        return captureTaskRepository.findById(id).map<CaptureTaskDetails>(Function<CaptureTask, CaptureTaskDetails> { this.getTaskDetails(it) }).orElse(null)
    }

    /*

      Private procedure

     */

    private fun getTaskDetails(captureTask: CaptureTask): CaptureTaskDetails {
        val captureTaskDetails = CaptureTaskDetails()
        captureTaskDetails.taskInfo = captureTask
        captureTaskDetails.runningTaskStatus = spiderMonitor.getSpiderStatus()[captureTask.id]
        return captureTaskDetails
    }

    private fun fetchTermCourseList(site: Site, termCode: String?): Collection<String> {
        val termList = HashMap<String, String>()

        Spider.create(CoursesListPageProcessor(site))
                .addUrl(KingoUrls.subjectListQueryPath + termCode!!)
                .addPipeline { resultItems, task -> termList.putAll(CoursesListPageProcessor.getCourseList(resultItems)) }
                .run()

        logger.debug("Fetched remote course list of term {}.", termCode)

        return termList.keys
    }

    private fun getTempDir(taskId: String): File {
        val file = File("./_temp/$taskId")
        if (!file.exists()) if (!file.mkdirs()) throw IllegalStateException("create_temp_folder_failed")
        val files = file.listFiles()
        if (files != null) Stream.of(*files).forEach(Consumer<File> { it.delete() })
        return file
    }

    private fun doLogin(caterpillarSetting: CaterpillarSetting): Site {

        val site = caterpillarSetting.createSite()

        val username = caterpillarSetting.username
        val password = caterpillarSetting.password
        val role = caterpillarSetting.role


        val items = HashMap<String, Any>()

        Spider.create(LoginPreparePageProcessor(username, password, role, site))
                .addUrl(KingoUrls.loginPageAddress)
                .addPipeline { r, t -> items.putAll(r.all) }
                .run()

        val loginResult = AtomicBoolean(false)

        if (!LoginPreparePageProcessor.getIsReady(items)) throw IllegalStateException("could_not_get_login_form")

        val cookies = LoginPreparePageProcessor.getCookie(items)
        if (cookies != null) site.addCookie("ASP.NET_SessionId", cookies["ASP.NET_SessionId"])

        val formFields = LoginPreparePageProcessor.getFormFields(items)

        val loginRequest = Request(KingoUrls.loginPageAddress)
        loginRequest.method = HttpConstant.Method.POST
        loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded")
        loginRequest.addHeader("Referer", KingoUrls.loginPageAddress)
        loginRequest.requestBody = HttpRequestBody.form(formFields, Objects.requireNonNull(caterpillarSetting.encoding))


        Spider.create(LoginExecutePageProcessor(site))
                .addRequest(loginRequest)
                .addPipeline { resultItems, task -> loginResult.set(LoginExecutePageProcessor.getIsLogin(resultItems)!!) }
                .run()

        logger.debug("Login to kingo {}.", if (loginResult.get()) "successful" else "failed")
        if (!loginResult.get()) throw BusinessException("login_to_kingo_failed")

        return site

    }

    private fun createSubjectRequest(site: Site, termCode: String?, subjectCode: String): Request {
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

    private fun changeCaptureTaskStatus(captureTask: CaptureTask, status: CaptureTaskStage?, description: String?) {
        if (status != null) captureTask.stage = status
        if (description != null) captureTask.stageReport = description
        captureTaskRepository.save(captureTask)
    }
}
