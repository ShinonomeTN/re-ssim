package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.service.statistics.StatisticsService
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dash")
class DashboardAPI(private val statisticsService: StatisticsService) {
    /**
     *
     * Get current running task counts
     *
     */
    @GetMapping("/dashes")
    @ApiDescription(title = "Get task status", description = "Get task status.")
    @RequiresPermissions("dash.task:get")
    fun dashboard(): Map<String, String> = statisticsService.dashBoard()
}
