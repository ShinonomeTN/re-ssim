package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.data.kingo.application.commons.ImportTaskEvent
import com.shinonometn.re.ssim.data.kingo.application.commons.ImportTaskStatus
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import com.shinonometn.re.ssim.data.kingo.application.entity.ImportTask
import com.shinonometn.re.ssim.data.kingo.application.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.data.kingo.application.repository.ImportTaskRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.task.TaskExecutor
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

/*
*
* Codes for import task events
*
* */

data class ImportTaskStageEvent(val taskId: Int, val status: Status, val message: String) {
    enum class Status {
        INITIALIZING, IMPORTING, CLEANING, TIDYING, FINISH
    }
}

private fun ImportTask.emitInitMessage() = ImportTaskStageEvent(this.captureTaskId!!, ImportTaskStageEvent.Status.INITIALIZING, "initialize")
private fun ImportTask.emitImportingMessage(message: String) = ImportTaskStageEvent(this.captureTaskId!!, ImportTaskStageEvent.Status.IMPORTING, message)
private fun ImportTask.emitFinishMessage() = ImportTaskStageEvent(this.captureTaskId!!, ImportTaskStageEvent.Status.FINISH, "finished")
private fun ImportTask.emitCleaningMessage() = ImportTaskStageEvent(this.captureTaskId!!, ImportTaskStageEvent.Status.CLEANING, "cleaning")

/*
*
* Service
*
* */

@Service
class ImportService(private val captureTaskRepository: CaptureTaskRepository,
                    private val importTaskRepository: ImportTaskRepository,
                    private val fileService: CaterpillarFileService,
                    private val courseDataService: CourseDataService,
                    private val caterpillarService: CaterpillarService,
                    private val taskExecutor: TaskExecutor,
                    private val transactionTemplate: TransactionTemplate,
                    private val redisLockRegistry: RedisLockRegistry,
                    private val applicationEventPublisher: ApplicationEventPublisher) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun getByTaskId(id: Int): Optional<ImportTask> {
        return importTaskRepository.findByCaptureTaskId(id)
    }

    fun deleteByTask(id: Int): Unit? = transactionTemplate.execute {
        importTaskRepository.deleteByCaptureTaskId(id)
    }

    /**
     * Start importing data to database
     *
     * @param taskId taskId
     * @return task base info
     */
    fun start(taskId: Int): CaptureTask {

        val captureTask = captureTaskRepository.findById(taskId)
                .orElseThrow { BusinessException("task_not_found") }

        caterpillarService.getSpiderStatus(taskId).ifPresent {
            if ("Running" == it.status) throw BusinessException("spider_running")
        }

        val dataFolder = File(fileService.contextOf(taskId).file, "data")

        if (!dataFolder.exists()) throw BusinessException("task_has_no_data")

        // Lock the task
//        val taskLock = obtainTermDataLock(captureTask)

        val importTask = importTaskRepository.save(getByTaskId(taskId)
                .orElse(ImportTask())
                .apply {
                    dataPath = dataFolder.path
                    captureTaskId = taskId
                    createDate = Date()
                })

        Flux.from<ImportTaskStageEvent> { e ->

            applicationEventPublisher.publishEvent(ImportTaskEvent(this, ImportTaskEvent.EventType.START, taskId))

            e.onNext(importTask.emitInitMessage())

            courseDataService.deleteVersion(captureTask.versionCode!!)

            Objects.requireNonNull<Array<File>>(dataFolder.listFiles()).forEach { file ->
                courseDataService.saveCourseInfo(JSON.read(FileInputStream(file), CourseEntity::class.java).apply {
                    batchId = captureTask.versionCode!!
                })

                e.onNext(importTask.emitImportingMessage(file.name))
            }
            logger.info("Batch data {} loading finished", captureTask.versionCode)

            e.onNext(importTask.emitCleaningMessage())

            val deleteResult = courseDataService.deleteOtherVersions(currentVersion = captureTask.versionCode!!)
            logger.info("Other version deleted, total {} records, current version {}",
                    deleteResult.deletedCount,
                    captureTask.versionCode!!
            )

            e.onNext(importTask.emitFinishMessage())
            e.onComplete()

        }.doOnError { e ->
            val deleteResult = courseDataService.deleteVersion(version = captureTask.versionCode!!)
            importTaskRepository.findByCaptureTaskId(taskId).ifPresent {
                it.status = ImportTaskStatus.ERROR
                importTaskRepository.save(it)
            }
            logger.error("Failure import rolled back, total {} record(s)", deleteResult.deletedCount, e)
            applicationEventPublisher.publishEvent(ImportTaskEvent(this, ImportTaskEvent.EventType.ERROR, taskId))
        }.doOnComplete {
            importTaskRepository.findByCaptureTaskId(taskId).ifPresent {
                it.status = ImportTaskStatus.FINISHED
                importTaskRepository.save(it)
            }
            applicationEventPublisher.publishEvent(ImportTaskEvent(this, ImportTaskEvent.EventType.FINISHED, taskId))
        }.doOnTerminate {
//            taskLock.unlock()
        }.subscribeOn(Schedulers.fromExecutor(taskExecutor)).subscribe {
            logger.debug("Import task $taskId @${it.status} : ${it.message}")
        }

        return captureTask
    }

    /*
    *
    *
    *
    * */

    private fun obtainTermDataLock(captureTask: CaptureTask): Lock {
        val taskLock = redisLockRegistry.obtain("${CaptureTask::class.simpleName}:${captureTask.termCode!!}")
        try {
            if (!taskLock.tryLock(500, TimeUnit.MILLISECONDS)) throw Exception("lock_failed")
        } catch (e: Exception) {
            logger.error("Could not obtain lock for term ${captureTask.termCode!!}", e)
            throw BusinessException("task_lock_obtain_failed")
        }

        return taskLock;
    }
}
