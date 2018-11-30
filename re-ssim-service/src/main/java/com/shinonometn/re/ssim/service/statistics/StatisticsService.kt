package com.shinonometn.re.ssim.service.statistics

import com.shinonometn.re.ssim.service.statistics.plugin.VisitorCounterStore
import org.springframework.stereotype.Service

@Service
class StatisticsService(private val visitorCounterStore: VisitorCounterStore) {

    fun increaseVisitorCount() = visitorCounterStore.increase()

    fun getVisitorCounts() = visitorCounterStore.get()
}
