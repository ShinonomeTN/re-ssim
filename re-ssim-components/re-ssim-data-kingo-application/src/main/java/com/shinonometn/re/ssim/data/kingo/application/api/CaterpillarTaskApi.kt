package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.ValidationMetaBuilder
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.commons.validation.helper.ValidateFunctions
import com.shinonometn.re.ssim.data.kingo.application.api.request.TaskCreateRequest
import com.shinonometn.re.ssim.data.kingo.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarProfileService
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarService
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
open class CaterpillarTaskApi(private val caterpillarService: CaterpillarService,
                              private val caterpillarProfileService: CaterpillarProfileService) {

    private val validator = Validator(ValidationMetaBuilder.create()
            .of(TaskCreateRequest::class.java)
            .addValidator("termCode", ValidateFunctions.notEmpty())
            .build()
    )

    @GetMapping("/{id}")
    fun getTask(@PathVariable("id") id: Int): Optional<CaptureTaskDetails> {
        return caterpillarService.queryTask(id)
    }

    @GetMapping
    fun listTasks(@PageableDefault pageable: Pageable): Page<CaptureTaskDetails> {
        return caterpillarService.listAllTasks(pageable)
    }

    @PostMapping
    fun createTask(@RequestBody request: TaskCreateRequest): CaptureTask {
        validator.validate(request)

        return caterpillarService.createTask(
                request.termCode
        )
    }

    @PostMapping("/{id}", params = ["stop"])
    fun stopTask(@PathVariable("id") id: Int): Optional<CaptureTaskDetails> {

        val captureTask = caterpillarService.getTask(id)
                .orElseThrow { BusinessException("task_not_found") }

        caterpillarService.stopTask(captureTask)

        return caterpillarService.queryTask(id)
    }

    @PostMapping("/{id}", params = ["resume"])
    fun resumeTask(@PathVariable("id") id: Int): Optional<CaptureTaskDetails> {
        val captureTask = caterpillarService.getTask(id)
                .orElseThrow { BusinessException("task_not_found") }

        caterpillarService.resumeTask(captureTask)

        return caterpillarService.queryTask(id)
    }

    @PostMapping("/{id}", params = ["start", "profile_id"])
    fun startTask(@PathVariable("id") id: Int, @RequestParam("profile_id") profileId: Int): CaptureTaskDetails {
        val caterpillarProfile = caterpillarProfileService.findById(profileId)
                .orElseThrow { BusinessException("profile_not_found") }

        return caterpillarService.startByTaskIdAndSettings(id, caterpillarProfile)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable("id") id: Int) {
        caterpillarService.delete(id)
    }

    @GetMapping("/{id}/bundle", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun bundle(@PathVariable("id") id: Int,
               @RequestParam("deleteOld", defaultValue = "false") deleteOld: Boolean): ResponseEntity<FileSystemResource> {

        val task = caterpillarService.getTask(id).orElseThrow { BusinessException("task_not_found") }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"task_${id}_bundle.zip\"")
                .body(FileSystemResource(caterpillarService.bundleData(task, deleteOld)))
    }

}