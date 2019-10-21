package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.pojo.TermLabelItem
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarProfileService
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/info")
class InfoApi(private val caterpillarService: CaterpillarService,
              private val caterpillarProfileService: CaterpillarProfileService) {

    @GetMapping("/term")
    fun listAllCachedTerms(): Collection<TermLabelItem> {
        return caterpillarService.cachedTermItemList()
    }

    @GetMapping("/term", params = ["refresh", "profile_id"])
    fun updateCachedTermList(@RequestParam("profile_id") caterpillarProfileId: Int): Collection<TermLabelItem> {
        val caterpillarProfile = caterpillarProfileService
                .findById(caterpillarProfileId)
                .orElseThrow { BusinessException("profile_not_found") }

        return caterpillarService.captureTermListFromRemote(caterpillarProfile)
    }

}