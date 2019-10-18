package com.shinonometn.re.ssim.caterpillar.application.service

import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage
import com.shinonometn.re.ssim.caterpillar.application.commons.TermLabelItem
import com.shinonometn.re.ssim.caterpillar.application.commons.agent.CaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.commons.agent.impl.KingoCaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.component.TermListCacheManager
import com.shinonometn.re.ssim.caterpillar.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.caterpillar.application.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.core.task.TaskExecutor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
open class CaterpillarService(private val fileManageService: CaterpillarFileManageService,
                              private val spiderMonitor: SpiderMonitor,
                              private val taskExecutor: TaskExecutor,
                              private val captureTaskRepository: CaptureTaskRepository,
                              private val transactionTemplate: TransactionTemplate,
                              private val termListCacheManager: TermListCacheManager) {

//    private val logger = LoggerFactory.getLogger("caterpillar_service")

    // TODO Use Factory Method
    fun requireAgentByProfile(caterpillarSetting: CaterpillarSetting): CaterpillarProfileAgent {

        if (caterpillarSetting.caterpillarProfile == null) throw BusinessException("caterpillar_profile_empty")

        return KingoCaterpillarProfileAgent(caterpillarSetting.caterpillarProfile!!).apply {
            bindSpiderMonitor(spiderMonitor)
        }
    }

    // TODO Use external cache
    private var cachedTermLabelItemList: Collection<TermLabelItem> = termListCacheManager.get()

    /*

        Status

    */

    /**
     * Get running spider counts
     *
     * @return long
     */
//    fun getCapturingTaskCount(): Long =
//            spiderMonitor.getSpiderStatus()
//                    .values
//                    .stream()
//                    .filter { i -> i != null && i.status == "Running" }
//                    .count()


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
        cachedTermLabelItemList = requireAgentByProfile(caterpillarSetting).fetchTerms()
        termListCacheManager.save(cachedTermLabelItemList)
        return cachedTermLabelItemList
    }

    fun cachedTermItemList() = cachedTermLabelItemList

    /*
     *
     *
     * Task management
     *
     *
     */

    /**
     * Create a termName capture task with code
     *
     * @param termCode termName code
     * @return created task
     */
    fun createTask(termCode: String, schoolIdentity: String): CaptureTask {
        val captureTask = CaptureTask()

        captureTask.createDate = Date()

        captureTask.termCode = termCode

        // TODO Use external method
        captureTask.termName = cachedTermLabelItemList
                .find { it.identity == termCode }?.title ?: throw BusinessException("term_not_exists")

        captureTask.stage = CaptureTaskStage.NONE
        captureTask.stageReport = "task_created"

        captureTask.schoolIdentity = schoolIdentity

        return captureTaskRepository.save(captureTask)
    }

    /**
     * Stop a task
     */
    fun stopTask(captureTask: CaptureTask) {

        val spiderStatus = spiderMonitor.getSpiderStatus()[captureTask.id.toString()]
                ?: throw BusinessException("task_have_not_initialized")

        spiderStatus.stop()

        changeCaptureTaskStatus(captureTask, CaptureTaskStage.STOPPED, "task_manually_stopped")
    }

    /**
     * Resume a stopped task
     */
    fun resumeTask(captureTask: CaptureTask): CaptureTaskDetails? {

        val spiderStatus = getTaskDetails(captureTask).status
                ?: throw BusinessException("task_have_not_initialized")

        if ("Running" == spiderStatus.name) throw BusinessException("spider_running")

        spiderStatus.start()

        changeCaptureTaskStatus(getTaskDetails(captureTask).task!!, CaptureTaskStage.CAPTURE, "task_resumed")

        return getTaskDetails(captureTask)
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

        fun updateTaskStatus(taskId: Int, stage: CaptureTaskStage, reporting: String) {
            transactionTemplate.execute {
                captureTaskRepository.updateTaskStatus(taskId, stage, reporting)
            }
        }

        val captureTaskDetails = captureTaskRepository
                .findById(taskId)
                .map { this.getTaskDetails(it) }
                .orElseThrow { BusinessException("task_not_exists") }

        if (captureTaskDetails.status != null) throw BusinessException("task_thread_exists")

        val captureTask = captureTaskDetails.task ?: throw Exception("unknown_exception")

        val taskUUID = taskId.toString()
        val termCode = captureTask.termCode ?: throw IllegalArgumentException("term_code_should_not_be_null")

        requireAgentByProfile(caterpillarSetting)
                .fetchCoursesData(taskUUID, termCode, fileManageService.dataFolderOfTask(taskId))
                .subscribeOn(Schedulers.fromExecutor(taskExecutor))
                .doOnError { error ->
                    updateTaskStatus(taskId, CaptureTaskStage.STOPPED, "Error: ${error.javaClass.name}, cause ${error.message}")
                }
                .subscribe { e ->
                    updateTaskStatus(taskId, e.stage, e.message)
                }

        captureTask.captureProfile = caterpillarSetting.caterpillarProfile
        captureTaskRepository.save(captureTask)

        return captureTaskDetails
    }

    /**
     * Delete a not running task
     *
     * @param id task id
     */
    fun delete(id: Int) {
        val spiderStatusMap = spiderMonitor.getSpiderStatus()

        spiderStatusMap[id.toString()]?.run {
            if ("Running" == status)
                throw BusinessException("spider_running")

            spiderMonitor.removeSpiderStatusMonitor(id.toString())
        }

        captureTaskRepository.deleteById(id)

        fileManageService.delete(id)
    }

    /**
     * Check if caterpillar setting valid
     *
     * @param caterpillarSetting settings
     * @return result
     */
    fun validateSettings(caterpillarSetting: CaterpillarSetting): Boolean {
        return try {
            requireAgentByProfile(caterpillarSetting).validateSetting()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get a task dto by id
     *
     * @param id id
     * @return dto
     */
    fun queryTask(id: Int): Optional<CaptureTaskDetails> {
        return captureTaskRepository.findById(id).map { this.getTaskDetails(it) }
    }

    /**
     * Get a task by id
     */
    fun getTask(id: Int): Optional<CaptureTask> {
        return captureTaskRepository.findById(id)
    }

    /**
     * Bundle data file
     */
    fun bundleData(task: CaptureTask, deleteOld: Boolean): File {

        if (CaptureTaskStage.STOPPED != task.stage) throw BusinessException("could_not_bundle_data_at_this_stage")

        getTaskDetails(task).status?.run {
            if ("Stopped" != status) throw BusinessException("could_not_export_data_when_spider_is_running")
        }

        val dataFolder = fileManageService.dataFolderOfTask(task.id)

        if (!dataFolder.exists()) throw BusinessException("task_has_no_data")

        val bundleFile = fileManageService.bundleFileOfTask(task.id)

        if (bundleFile.exists()) {
            when (deleteOld) {
                true -> FileUtils.deleteQuietly(bundleFile)
                else -> return bundleFile
            }
        }

        ZipOutputStream(FileOutputStream(bundleFile)).use { zip ->
            dataFolder.listFiles { _, name -> name.endsWith(".json") }.forEach { jsonFile ->
                zip.putNextEntry(ZipEntry(jsonFile.name))
                IOUtils.copy(FileInputStream(jsonFile), zip)
                zip.closeEntry()
            }
        }

        return bundleFile
    }

    /*

      Private procedure

     */

    private fun getTaskDetails(captureTask: CaptureTask): CaptureTaskDetails {
        val captureTaskDetails = CaptureTaskDetails()
        captureTaskDetails.task = captureTask
        captureTaskDetails.status = spiderMonitor.getSpiderStatus()[captureTask.id.toString()]
        return captureTaskDetails
    }

    private fun changeCaptureTaskStatus(captureTask: CaptureTask, status: CaptureTaskStage?, description: String?) {
        if (status != null) captureTask.stage = status
        if (description != null) captureTask.stageReport = description
        captureTaskRepository.save(captureTask)
    }
}
