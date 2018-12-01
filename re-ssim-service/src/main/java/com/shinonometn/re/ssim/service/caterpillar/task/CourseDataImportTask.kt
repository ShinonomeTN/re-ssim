package com.shinonometn.re.ssim.service.caterpillar.task

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.commons.file.fundation.FileContext
import com.shinonometn.re.ssim.service.caterpillar.ImportTaskService
import com.shinonometn.re.ssim.service.caterpillar.commons.ImportTaskStatus
import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask
import com.shinonometn.re.ssim.service.caterpillar.plugin.CaterpillarMonitorStore
import com.shinonometn.re.ssim.service.courses.CourseInfoService
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class CourseDataImportTask(private val importTaskService: ImportTaskService,
                           private val courseInfoService: CourseInfoService,
                           private val importTask: ImportTask,
                           private val caterpillarMonitorStore: CaterpillarMonitorStore,
                           private val dataFolder: FileContext) : Runnable {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val batchId = importTask.id

    override fun run() {
        try {

            importTask.status = ImportTaskStatus.IMPORTING
            importTask.statusReport = "load_data_to_database"
            importTaskService.save(importTask)
            caterpillarMonitorStore.increaseCaptureTaskCount()

            loadDataToDatabase()

            deleteOtherData()

            importTask.status = ImportTaskStatus.FINISHED
            importTask.statusReport = "finished"
            importTask.finishDate = Date()
            importTaskService.save(importTask)

        } catch (e: IOException) {
            logger.error("Something happen while importing files, reversing. Batch Id :$batchId", e)
            importTask.status = ImportTaskStatus.ERROR
            importTask.statusReport = Optional.ofNullable(e.message).orElse(e.javaClass.name)
            importTaskService.save(importTask)
            cleanOnFail()
        } finally {
            caterpillarMonitorStore.decreaseCaptureTaskCount()
        }
    }

    private fun loadDataToDatabase() {
        val folder = dataFolder.file
        if (!folder.isDirectory) throw BusinessException("temp_dir_not_found")
        Objects.requireNonNull<Array<File>>(folder.listFiles()).forEach { file ->
            val courseEntity = JSON.read(FileInputStream(file), CourseEntity::class.java)
            courseEntity.batchId = batchId
            courseInfoService.save(courseEntity)
        }

        logger.info("Batch data {} loading finished", batchId)
    }

    private fun deleteOtherData() {
        val deleteResult = courseInfoService.deleteOtherVersions(batchId)

        logger.info("Other version deleted, total {} records, current version {}",
                deleteResult.deletedCount,
                batchId)
    }

    private fun cleanOnFail() {
        val deleteResult = courseInfoService.deleteVersion(batchId)

        logger.info("Failure import rolled back, total {} record(s)", deleteResult.deletedCount)
    }
}