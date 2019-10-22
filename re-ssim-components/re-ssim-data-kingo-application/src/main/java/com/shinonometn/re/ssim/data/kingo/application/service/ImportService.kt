package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.data.kingo.application.commons.ImportTaskStatus
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import com.shinonometn.re.ssim.data.kingo.application.entity.ImportTask
import com.shinonometn.re.ssim.data.kingo.application.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.data.kingo.application.repository.ImportTaskRepository
import org.slf4j.LoggerFactory
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.FileInputStream
import java.util.*

@Service
class ImportService(private val captureTaskRepository: CaptureTaskRepository,
                    private val importTaskRepository: ImportTaskRepository,
                    private val fileService: CaterpillarFileService,
                    private val courseDataService: CourseDataService,
                    private val caterpillarService: CaterpillarService,
                    private val taskExecutor: TaskExecutor,
                    private val transactionTemplate: TransactionTemplate) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    open fun getByTaskId(id: Int): Optional<ImportTask> {
        return importTaskRepository.findByCaptureTaskId(id)
    }

    data class ImportTaskEvent(val taskId: Int, val status: Status, val message: String) {
        enum class Status {
            INITIALIZING, IMPORTING, CLEANING, FINISH
        }
    }

    private fun ImportTask.emitInitMessage() = ImportTaskEvent(this.captureTaskId!!, ImportTaskEvent.Status.INITIALIZING, "initialize")
    private fun ImportTask.emitImportingMessage(message: String) = ImportTaskEvent(this.captureTaskId!!, ImportTaskEvent.Status.IMPORTING, message)
    private fun ImportTask.emitFinishMessage() = ImportTaskEvent(this.captureTaskId!!, ImportTaskEvent.Status.FINISH, "finished")
    private fun ImportTask.emitCleaningMessage() = ImportTaskEvent(this.captureTaskId!!, ImportTaskEvent.Status.CLEANING, "cleaning")

    /**
     * Start importing data to database
     *
     * @param taskId taskId
     * @return task base info
     */
    open fun start(taskId: Int): CaptureTask {

        val captureTask = captureTaskRepository.findById(taskId).orElse(null)
                ?: throw BusinessException("task_not_found")

        caterpillarService.getSpiderStatus(taskId).ifPresent {
            if ("Running" == it.status) throw BusinessException("spider_running")
        }

        val dataFolder = File(fileService.contextOf(taskId).file, "data")

        if (!dataFolder.exists()) throw BusinessException("task_has_no_data")

        val importTask = importTaskRepository.save(getByTaskId(taskId)
                .orElse(ImportTask())
                .apply {
                    dataPath = dataFolder.path
                    captureTaskId = taskId
                    createDate = Date()
                })

        Flux.from<ImportTaskEvent> { e ->

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
        }.doOnComplete {
            importTaskRepository.findByCaptureTaskId(taskId).ifPresent {
                it.status = ImportTaskStatus.FINISHED
                importTaskRepository.save(it)
            }
        }.subscribeOn(Schedulers.fromExecutor(taskExecutor)).subscribe {
            logger.debug("Import task $taskId @${it.status} : ${it.message}")
        }

        return captureTask
    }

    fun deleteByTask(id: Int): Unit? = transactionTemplate.execute {
        importTaskRepository.deleteByCaptureTaskId(id)
    }
}
