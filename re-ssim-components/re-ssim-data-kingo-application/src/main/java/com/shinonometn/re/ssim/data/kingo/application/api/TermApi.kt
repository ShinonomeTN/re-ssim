package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import com.shinonometn.re.ssim.data.kingo.application.service.TermManageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/term")
class TermApi(private val termManageService: TermManageService) {

    @GetMapping
    fun list(@PageableDefault pageable: Pageable): Page<TermInfo> = termManageService.list(pageable)

    @PostMapping("/{id}/calendar", params = ["refresh", "profileId"])
    fun refreshCalendar(@PathVariable("id") id: Int, @RequestParam("profileId") profileId: Int): TermInfo {
        val termInfo = termManageService
                .find(id)
                .orElseThrow { BusinessException("term_not_found") }

        return termManageService.refreshTermCalendarInfo(termInfo.identity!!, profileId)
    }

}
