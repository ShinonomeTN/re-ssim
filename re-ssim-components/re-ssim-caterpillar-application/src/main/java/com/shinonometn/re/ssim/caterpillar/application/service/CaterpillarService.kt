package com.shinonometn.re.ssim.caterpillar.application.service

import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage
import com.shinonometn.re.ssim.caterpillar.application.commons.TermLabelItem
import com.shinonometn.re.ssim.caterpillar.application.commons.agent.CaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.commons.agent.impl.KingoCaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.caterpillar.application.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls
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
import java.util.*
import java.util.stream.Stream

@Service
class CaterpillarService(private val fileManageService: CaterpillarFileManageService,
                         private val spiderMonitor: SpiderMonitor,
                         private val taskExecutor: TaskExecutor,
                         private val captureTaskRepository: CaptureTaskRepository) {

    private val logger = LoggerFactory.getLogger(CaterpillarService::class.java)

    // TODO Use Factory Method
    private fun requireAgentByProfile(caterpillarSetting: CaterpillarSetting): CaterpillarProfileAgent {

        if (caterpillarSetting.caterpillarProfile == null) throw BusinessException("caterpillar_profile_empty")

        return KingoCaterpillarProfileAgent(caterpillarSetting.caterpillarProfile!!)
    }

    // TODO Use external cache
    private var cachedTermLabelItemList: Collection<TermLabelItem> = Collections.emptyList()

    /*

        Status

    */

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
     * Get all tasks
     *
     * @return task list
     */
    fun listAllTasks(pageable: Pageable): Page<CaptureTaskDetails> {
        return captureTaskRepository.findAll(pageable).map { this.getTaskDetails(it) }
    }

    /**
     * Get all school terms
     *
     *
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    fun captureTermListFromRemote(caterpillarSetting: CaterpillarSetting): Collection<TermLabelItem> {
        this.cachedTermLabelItemList = requireAgentByProfile(caterpillarSetting).fetchTerms()
        return cachedTermLabelItemList;
    }

    fun cachedTermItemList() = this.cachedTermLabelItemList

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

        // TODO Use external method
        captureTask.termName = cachedTermLabelItemList
                .find { it.title == termCode }?.title ?: throw BusinessException("term_not_exists")

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
    fun stopTaskById(taskId: Int): CaptureTaskDetails? {
        val captureTask = captureTaskRepository.findById(taskId).orElse(null) ?: return null

        val spiderStatus = spiderMonitor.getSpiderStatus()[taskId.toString()]
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
    fun resumeTaskById(taskId: Int): CaptureTaskDetails? {
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
    fun startByTaskIdAndSettings(taskId: Int, caterpillarSetting: CaterpillarSetting): CaptureTaskDetails {

        val captureTaskDetails = captureTaskRepository
                .findById(taskId)
                .map { this.getTaskDetails(it) }
                .orElseThrow { BusinessException("task_not_exists") }

        if (captureTaskDetails.runningTaskStatus != null) throw BusinessException("task_thread_exists")

        val captureTask = captureTaskDetails.taskInfo

        // TODO Refactoring this using RxJava
        changeCaptureTaskStatus(captureTask, CaptureTaskStage.INITIALIZE, "task_initialing")
//        emitTaskCreateMessage()

        taskExecutor.execute {


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
    fun delete(id: Int) {
        val spiderStatusMap = spiderMonitor.getSpiderStatus()

        if (spiderStatusMap.containsKey(id.toString())) {

            val spiderStatus = spiderStatusMap[id.toString()]
            if (Spider.Status.Running.name == spiderStatus?.status)
                throw BusinessException("spider_running")

            spiderMonitor.removeSpiderStatusMonitor(id.toString())
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
    fun queryTask(id: Int): CaptureTaskDetails? {
        return captureTaskRepository.findById(id).map { this.getTaskDetails(it) }.orElse(null)
    }

    /*

      Private procedure

     */

    // TODO Put spider business into ProfileAgents

    private fun getTaskDetails(captureTask: CaptureTask): CaptureTaskDetails {
        val captureTaskDetails = CaptureTaskDetails()
        captureTaskDetails.taskInfo = captureTask
        captureTaskDetails.runningTaskStatus = spiderMonitor.getSpiderStatus()[captureTask.id.toString()]
        return captureTaskDetails
    }

    private fun requireTempFolder(taskId: Int): File {
        val file = File(fileManageService.contextOf(taskId).file, "/capturing")
        if (!file.exists()) if (!file.mkdirs()) throw IllegalStateException("create_temp_folder_failed")
        val files = file.listFiles()
        if (files != null) Stream.of(*files).forEach { it.delete() }
        return file
    }



    private fun changeCaptureTaskStatus(captureTask: CaptureTask, status: CaptureTaskStage?, description: String?) {
        if (status != null) captureTask.stage = status
        if (description != null) captureTask.stageReport = description
        captureTaskRepository.save(captureTask)
    }
}
