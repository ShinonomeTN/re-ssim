package com.shinonometn.re.ssim.service.caterpillar

import com.fasterxml.jackson.annotation.JsonIgnore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.scheduler.MonitorableScheduler

import java.util.Date

class SpiderStatusImpl(@field:JsonIgnore
                       @get:JsonIgnore
                       val spider: Spider, protected val monitorSpiderListener: SpiderMonitorListener) : SpiderStatus {

    protected var logger = LoggerFactory.getLogger(javaClass)

    override val name: String
        get() = spider.uuid

    override val leftPageCount: Int
        get() {
            if (spider.scheduler is MonitorableScheduler) {
                return (spider.scheduler as MonitorableScheduler).getLeftRequestsCount(spider)
            }
            logger.warn("Get leftPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!")
            return -1
        }

    override val totalPageCount: Int
        get() {
            if (spider.scheduler is MonitorableScheduler) {
                return (spider.scheduler as MonitorableScheduler).getTotalRequestsCount(spider)
            }
            logger.warn("Get totalPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!")
            return -1
        }

    override val successPageCount: Int
        get() = monitorSpiderListener.successCount.get()

    override val errorPageCount: Int
        get() = monitorSpiderListener.errorCount.get()

    override val errorPages: List<String>
        get() = monitorSpiderListener.getErrorUrls()

    override val status: String
        get() = spider.status.name

    override val thread: Int
        get() = spider.threadAlive

    override val startTime: Date
        get() = spider.startTime

    override val pagePerSecond: Int
        get() {
            val runSeconds = (System.currentTimeMillis() - startTime.time).toInt() / 1000
            return if (runSeconds == 0) 0 else successPageCount / runSeconds
        }

    override fun start() {
        spider.start()
    }

    override fun stop() {
        spider.stop()
    }

}
