package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.agent.KingoCaterpillarProfileAgent
import com.shinonometn.re.ssim.data.kingo.application.component.TermListCacheManager
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.data.kingo.application.pojo.TermLabelItem
import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers
import java.io.File
import java.util.*

@Service
open class CaterpillarService(private val fileService: CaterpillarFileService,
                              private val spiderMonitor: SpiderMonitor,
                              private val taskExecutor: TaskExecutor,
                              private val termListCacheManager: TermListCacheManager) {

//    private val logger = LoggerFactory.getLogger("caterpillar_service")

    private var cachedTermLabelItemList: Collection<TermLabelItem> = termListCacheManager.get()

    /*

        Status

    */

    open fun getSpiderStatus(id: Int): Optional<SpiderStatus> {
        return Optional.ofNullable(spiderMonitor.getSpiderStatus()[id.toString()])
    }

    /**
     * Get all school terms
     *
     *
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    open fun captureTermListFromRemote(caterpillarSetting: CaterpillarSetting): Collection<TermLabelItem> {
        cachedTermLabelItemList = KingoCaterpillarProfileAgent(caterpillarSetting.caterpillarProfile!!).fetchTerms()
        termListCacheManager.save(cachedTermLabelItemList)
        return cachedTermLabelItemList
    }

    open fun cachedTermItemList() = cachedTermLabelItemList

    /*
     *
     *
     * Task management
     *
     *
     */

    /**
     * Stop a task
     */
    open fun stopTask(id: Int): SpiderStatus {
        val spiderStatus = spiderMonitor.getSpiderStatus()[id.toString()]
                ?: throw BusinessException("task_have_not_initialized")

        spiderStatus.stop()

        return spiderStatus
    }

    /**
     * Resume a stopped task
     */
    open fun resumeTask(id : Int): SpiderStatus {

        val spiderStatus = spiderMonitor.getSpiderStatus()[id.toString()]
                ?: throw BusinessException("task_have_not_initialized")

        if ("Running" == spiderStatus.name) throw BusinessException("spider_running")

        spiderStatus.start()

        return spiderStatus
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
    open fun start(captureTask: CaptureTask): Optional<SpiderStatus> {

//        fun updateTaskStatus(taskId: Int, stage: CaptureTaskStage, reporting: String) {
//            transactionTemplate.execute {
//                captureTaskRepository.updateTaskStatus(taskId, stage, reporting)
//            }
//        }

        val spiderStatus = getSpiderStatus(captureTask.id!!)

        if (spiderStatus.isPresent) throw BusinessException("task_thread_exists")

        val taskId = captureTask.id ?: throw IllegalArgumentException("unexpected_task_id_null")
        val taskUUID = taskId.toString()
        val termCode = captureTask.termCode ?: throw IllegalArgumentException("term_code_should_not_be_null")

        KingoCaterpillarProfileAgent(captureTask.captureProfile ?: throw BusinessException("caterpillar_profile_empty"))
                .fetchCoursesData(taskUUID, termCode, fileService.dataFolderOfTask(taskId))
                .subscribeOn(Schedulers.fromExecutor(taskExecutor))
                .doOnError { error ->
                    //                    updateTaskStatus(taskId, CaptureTaskStage.STOPPED, "Error: ${error.javaClass.name}, cause ${error.message}")
                }
                .subscribe { e ->
                    //                    updateTaskStatus(taskId, e.stage, e.message)
                }

        return getSpiderStatus(taskId)
    }


    /**
     * Check if caterpillar setting valid
     *
     * @param caterpillarSetting settings
     * @return result
     */
    open fun validateSettings(caterpillarSetting: CaterpillarSetting): Boolean {
        return try {
            KingoCaterpillarProfileAgent(caterpillarSetting.caterpillarProfile!!).validateSetting()
            true
        } catch (e: Exception) {
            false
        }
    }

    open fun removeSpider(id: Int) {
        spiderMonitor.removeSpiderStatusMonitor(id.toString())
        fileService.delete(id)
    }

    open fun getDataFolderOf(id: Int): Optional<File> {
        val file = fileService.dataFolderOfTask(id)
        return if (file.exists() && file.isDirectory) Optional.of(file) else Optional.empty()
    }

    /*

      Private procedure

     */
}
