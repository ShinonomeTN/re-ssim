package com.shinonometn.re.ssim.application.controller.management.data

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.caterpillar.CaterpillarTaskService
import com.shinonometn.re.ssim.service.caterpillar.ImportTaskService
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask
import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/import")
class DataImportTaskManageAPI(private val caterpillarTaskService: CaterpillarTaskService,
                              private val dataImportTaskService: ImportTaskService) {
    /**
     *
     * Start import task data to DB
     *
     */
    @PostMapping("/{id}")
    @ApiDescription(title = "Import task data", description = "Import a finshed capture task.")
    @RequiresPermissions("import:start")
    fun import(@PathVariable("id") id: String): CaptureTask {

        val captureTaskDetails = caterpillarTaskService.queryTask(id) ?: throw BusinessException("task_not_found")

        if (captureTaskDetails.taskInfo.finished) throw BusinessException("task_finished")

        if (captureTaskDetails.runningTaskStatus != null && captureTaskDetails.runningTaskStatus!!.status == "Running")
            throw BusinessException("spider_running")

        return dataImportTaskService.start(captureTaskDetails.taskInfo.id)
    }

    @GetMapping("/{id}")
    @ApiDescription(title = "Get a import task", description = "Get a import task by id")
    @RequiresPermissions("import:read")
    fun get(@PathVariable("id") id: String): ImportTask? =
            dataImportTaskService.findOne(id).orElse(null)

    @GetMapping
    @ApiDescription(title = "Get import tasks", description = "Get all import tasks")
    @RequiresPermissions("import:read")
    fun listImportTasks(@PageableDefault pageable: Pageable): Page<ImportTask> {
        return dataImportTaskService.list(pageable)
    }

    @DeleteMapping("/{id}")
    @ApiDescription(title = "Delete import task", description = "Delete a import task")
    @RequiresPermissions("import:delete")
    fun deleteImportTask(@PathVariable("id") id: String): RexModel<Any> {
        dataImportTaskService.delete(id)
        return RexModel.success()
    }
}
