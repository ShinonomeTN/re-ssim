package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.ValidationMetaBuilder
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.data.kingo.application.dto.CaterpillarSettingsDto
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.entity.CaterpillarSetting
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarProfileService
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/profile")
open class CaterpillarProfileApi(private val caterpillarProfileService: CaterpillarProfileService,
                                 private val caterpillarService: CaterpillarService) {

    private val validator = Validator(ValidationMetaBuilder
            .create()

            .of(CaterpillarSetting::class.java)
            .addValidator("caterpillarProfile", { o -> o is Map<*, *> && o.size > 0 }, "caterpillar_profile_empty")

            .build()
    )

    /**
     * List all settings in page
     */
    @GetMapping
    open fun list(@PageableDefault pageable: Pageable): Page<CaterpillarSetting> {
        return caterpillarProfileService.findAll(pageable)
    }

    /**
     * Get a caterpillar setting
     */
    @GetMapping("/{id}")
    open fun get(@PathVariable("id") id: Int): Optional<CaterpillarSettingsDto> =
            caterpillarProfileService.findById(id).map {
                CaterpillarSettingsDto().apply {
                    this.caterpillarSetting = it
                    this.agentProfileInfo = caterpillarService.requireAgentByProfile(it)
                }
            }

    /**
     * Save a caterpillar setting
     */
    @PostMapping
    open fun save(@RequestBody caterpillarSetting: CaterpillarSetting): CaterpillarSetting {
        validator.validate(caterpillarSetting)
        return caterpillarProfileService.save(caterpillarSetting)
    }

    @GetMapping("/{id}", params = ["validate"])
    open fun validate(@PathVariable("id") settingsId: Int): Boolean {
        val caterpillarSetting = caterpillarProfileService
                .findById(settingsId)
                .orElseThrow { BusinessException("setting_not_found") }

        return caterpillarService.validateSettings(caterpillarSetting)
    }

    @GetMapping(params = ["owner"])
    open fun findByUser(@RequestParam("owner") owner: String,
                        @PageableDefault pageable: Pageable): Page<CaterpillarSetting> {
        return caterpillarProfileService.findAllByUser(owner, pageable)
    }

    @GetMapping(params = ["owner", "profile_name"])
    open fun getByUserAndProfileName(@RequestParam("owner") owner: String,
                                     @RequestParam("profile_name") profileName: String): Optional<CaterpillarSetting> {
        return caterpillarProfileService.findProfile(owner, profileName)
    }
}
