package com.shinonometn.re.ssim.caterpillar.application.controller

import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.caterpillar.application.service.CaterpillarProfileService
import com.shinonometn.re.ssim.commons.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profiles")
open class CaterpillarProfileApi(val caterpillarProfileService: CaterpillarProfileService) {

    /**
     * List all settings in page
     */
    @GetMapping
    fun list(@PageableDefault pageable: Pageable): Page<CaterpillarSetting> {
        return caterpillarProfileService.findAll(pageable)
    }

    /**
     * Get a caterpillar setting
     */
    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Int): CaterpillarSetting {
        return caterpillarProfileService
                .findById(id)
                .orElseThrow { BusinessException("profile_not_found") }
    }

    /**
     * Save a caterpillar setting
     */
    @PostMapping
    fun save(@RequestBody caterpillarSetting: CaterpillarSetting): CaterpillarSetting {
        return caterpillarProfileService.save(caterpillarSetting)
    }
}
