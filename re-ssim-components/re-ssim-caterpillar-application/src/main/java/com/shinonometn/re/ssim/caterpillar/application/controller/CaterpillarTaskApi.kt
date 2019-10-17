package com.shinonometn.re.ssim.caterpillar.application.controller

import com.shinonometn.re.ssim.caterpillar.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.caterpillar.application.service.CaterpillarProfileService
import com.shinonometn.re.ssim.caterpillar.application.service.CaterpillarService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/task")
open class CaterpillarTaskApi(private val caterpillarService: CaterpillarService,
                              private val caterpillarProfileService: CaterpillarProfileService) {

    @GetMapping
    fun listTasks(@PageableDefault pageable: Pageable) : Page<CaptureTaskDetails> {
        return caterpillarService.listAllTasks(pageable)
    }



}