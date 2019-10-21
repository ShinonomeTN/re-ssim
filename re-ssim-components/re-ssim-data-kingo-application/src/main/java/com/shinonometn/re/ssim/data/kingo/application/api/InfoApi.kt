package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.pojo.TermLabelItem
import com.shinonometn.re.ssim.data.kingo.application.service.CaterpillarSettingsService
import com.shinonometn.re.ssim.data.kingo.application.service.CaterpillarService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/info")
class InfoApi(private val caterpillarService: CaterpillarService,
              private val caterpillarSettingsService: CaterpillarSettingsService) {

    @GetMapping("/term")
    fun listAllCachedTerms(): Collection<TermLabelItem> {
        return caterpillarService.cachedTermItemList()
    }

    @GetMapping("/term", params = ["refresh", "profile_id"])
    fun updateCachedTermList(@RequestParam("profile_id") caterpillarProfileId: Int): Collection<TermLabelItem> {
        val caterpillarProfile = caterpillarSettingsService
                .findById(caterpillarProfileId)
                .orElseThrow { BusinessException("profile_not_found") }

        return caterpillarService.captureTermListFromRemote(caterpillarProfile)
    }

}