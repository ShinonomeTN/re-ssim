package com.shinonometn.re.ssim.service.statistics

import com.shinonometn.re.ssim.service.caterpillar.plugin.CaterpillarMonitorStore
import com.shinonometn.re.ssim.service.statistics.plugin.VisitorCounterStore
import org.springframework.stereotype.Service

@Service
class StatisticsService(private val visitorCounterStore: VisitorCounterStore,
                        private val caterpillarMonitorStore: CaterpillarMonitorStore) {

    fun increaseVisitorCount() = visitorCounterStore.increase()

    fun getVisitorCounts(): Long {
        return visitorCounterStore.get()
    }

    fun dashBoard(): MutableMap<String, Long>? = caterpillarMonitorStore.all
}
