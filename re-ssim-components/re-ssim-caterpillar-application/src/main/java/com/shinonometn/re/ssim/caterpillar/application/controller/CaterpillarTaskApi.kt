package com.shinonometn.re.ssim.caterpillar.application.controller

import com.shinonometn.re.ssim.caterpillar.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import com.shinonometn.re.ssim.caterpillar.application.service.CaterpillarProfileService
import com.shinonometn.re.ssim.caterpillar.application.service.CaterpillarService
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.ValidationMetaBuilder
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.commons.validation.helper.ValidateFunctions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/task")
open class CaterpillarTaskApi(private val caterpillarService: CaterpillarService,
                              private val caterpillarProfileService: CaterpillarProfileService) {

    private val validator = Validator(ValidationMetaBuilder
            .create()

            .of(TaskCreateRequest::class.java)
            .addValidator("termCode", ValidateFunctions.notEmpty())
            .addValidator("schoolIdentity", ValidateFunctions.notEmpty())

            .build())

    @GetMapping
    fun listTasks(@PageableDefault pageable: Pageable): Page<CaptureTaskDetails> {
        return caterpillarService.listAllTasks(pageable)
    }

    data class TaskCreateRequest(private val map: Map<String, Any?> = HashMap(2)) {
        val termCode: String? by map
        val schoolIdentity: String? by map
    }

    @PostMapping
    fun createTask(@RequestBody request: TaskCreateRequest): CaptureTask {
        validator.validate(request)
        return caterpillarService.createTask(
                request.termCode!!,
                request.schoolIdentity!!
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

    @PostMapping("/{id}", params = ["stop"])
    fun deleteTask(@PathVariable("id") id: Int) {
        caterpillarService.delete(id)
    }
}