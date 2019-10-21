package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.ValidationMetaBuilder
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.commons.validation.helper.ValidateFunctions
import com.shinonometn.re.ssim.data.kingo.application.api.request.TaskCreateRequest
import com.shinonometn.re.ssim.data.kingo.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.service.*
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus
import org.springframework.core.io.FileSystemResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/task")
open class TaskApi(private val taskService: TaskService,
                   private val caterpillarService: CaterpillarService,
                   private val bundleService: BundleService,
                   private val importService: ImportService,
                   private val caterpillarSettingsService: CaterpillarSettingsService) {

    private val validator = Validator(ValidationMetaBuilder.create()
            .of(TaskCreateRequest::class.java)
            .addValidator("termCode", ValidateFunctions.notEmpty())
            .addValidator("profileId", ValidateFunctions.notNull())
            .build()
    )

    /*
    *
    * Task management
    *
    * */

    @GetMapping("/{id}")
    fun getTask(@PathVariable("id") id: Int): Optional<CaptureTaskDetails> {
        return taskService.get(id)
    }

    @GetMapping
    fun listTasks(@PageableDefault pageable: Pageable): Page<CaptureTaskDetails> {
        return taskService.findAll(pageable)
    }

    @PostMapping
    fun createTask(@RequestBody request: TaskCreateRequest): CaptureTask {
        validator.validate(request)

        val caterpillarProfile = caterpillarSettingsService
                .findById(request.profileId!!)
                .orElseThrow { BusinessException("profile_not_found") }

        return taskService.create(request.termCode!!, caterpillarProfile)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable("id") id: Int) = taskService.delete(id)

    /*
    *
    * Capturing
    *
    * */

    @PostMapping("/{id}/capturing", params = ["stop"])
    fun stop(@PathVariable("id") id: Int) = caterpillarService.stopTask(id)

    @PostMapping("/{id}/capturing", params = ["resume"])
    fun resumeTask(@PathVariable("id") id: Int) = caterpillarService.resumeTask(id)

    @PostMapping("/{id}/capturing", params = ["start"])
    fun startTask(@PathVariable("id") id: Int): Optional<SpiderStatus> {
        val task = taskService.get(id).orElseThrow { BusinessException("task_not_exists") }
        return caterpillarService.start(task.taskInfo!!)
    }

    @GetMapping("/{id}/bundle", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun bundle(@PathVariable("id") id: Int,
               @RequestParam("deleteOld", defaultValue = "false") deleteOld: Boolean): ResponseEntity<FileSystemResource> {

        val task = taskService.get(id).orElseThrow { BusinessException("task_not_found") }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"task_${id}_bundle.zip\"")
                .body(FileSystemResource(bundleService.bundleData(task.taskInfo!!, deleteOld)))
    }

    /*
    *
    * Importing
    *
    * */

    @PostMapping("/{id}/import", params = ["start"])
    fun import(@PathVariable("id") id: Int) = importService.start(id)
}