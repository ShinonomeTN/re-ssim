package com.shinonometn.re.ssim.service.statistics

import com.shinonometn.re.ssim.service.statistics.plugin.VisitorCounterPlugin
import org.springframework.stereotype.Service

@Service
class StatisticsService(private val visitorCounterPlugin: VisitorCounterPlugin) {

    fun increaseVisitorCount() = visitorCounterPlugin.increase()

    fun getVisitorCounts() = visitorCounterPlugin.get()
}
