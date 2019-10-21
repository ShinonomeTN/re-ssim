package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.dto.TaskBundleInfo
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
open class BundleService(private val caterpillarFileService: CaterpillarFileService,
                         private val bundleFileService: BundleFileService,
                         private val caterpillarService: CaterpillarService) {

    /**
     * Bundle data file
     */
    open fun bundleData(task: CaptureTask, deleteOld: Boolean): File {

        val spiderStatus = caterpillarService.getSpiderStatus(task.id!!)

        spiderStatus.ifPresent {
            if ("Stopped" != it.status) throw BusinessException("could_not_export_data_when_spider_is_running")
        }

        val dataFolder = caterpillarFileService.dataFolderOfTask(task.id)

        if (!dataFolder.exists()) throw BusinessException("task_has_no_data")

        val folder = bundleFileService.contextOf(task.id).file
        if (!folder.exists() && !folder.mkdir()) throw Exception("could_not_create_folder: ${folder.absolutePath}")
        val bundleFile = File(folder, "bundle.zip")

        if (bundleFile.exists()) {
            when (deleteOld) {
                true -> FileUtils.deleteQuietly(bundleFile)
                else -> return bundleFile
            }
        }

        ZipOutputStream(FileOutputStream(bundleFile)).use { zip ->
            dataFolder.listFiles { _, name -> name.endsWith(".json") }?.forEach { jsonFile ->
                zip.putNextEntry(ZipEntry(jsonFile.name))
                IOUtils.copy(FileInputStream(jsonFile), zip)
                zip.closeEntry()
            }
        }

        return bundleFile
    }

    open fun hasBundleData(task: CaptureTask): Boolean {
        val context = bundleFileService.contextOf(task.id)
        return File(context.file, "bundle.zip").exists()
    }

    open fun deleteByTask(id: Int) {
        bundleFileService.delete(id)
    }

    fun getBundleInfo(task: CaptureTask): TaskBundleInfo {
        val bundleFile = File(bundleFileService.contextOf(task.id).file, "bundle.zip")
        return TaskBundleInfo().apply {
            this.hasBundleFile = bundleFile.exists()
            if (hasBundleFile) this.bundleFileSize = bundleFile.totalSpace

            val folder = caterpillarFileService.dataFolderOfTask(task.id)

            if (!folder.exists()) {
                this.hasBundleFile = false
                return@apply
            }

            this.fileCount = folder.listFiles { file -> file.name.endsWith(".json") }?.count() ?: 0
        }
    }
}