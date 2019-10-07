package com.shinonometn.re.ssim.service.caterpillar

import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.SpiderListener
import java.util.*
import java.util.stream.Stream

/**
 *
 * Spider monitor
 *
 * Spider monitor is used for registering spiders to a list for managing.
 *
 */
class SpiderMonitor {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    //    private AtomicBoolean started = new AtomicBoolean(false);

    private val spiderStatus = HashMap<String, SpiderStatus>()

    fun getSpiderStatus(): Map<String, SpiderStatus> = spiderStatus

    fun removeSpiderStatusMonitor(id: String) = spiderStatus.remove(id)

    @Synchronized
    fun register(vararg spiders: Spider): SpiderMonitor {
        Stream.of(*spiders).forEach { spider ->
            val spiderMonitorListener = SpiderMonitorListener()

            /**
             *
             * If spider already has listeners, get the list and add the monitor listener,
             * else, create a new array list and put the listener in.
             *
             */
            if (spider.spiderListeners == null) {
                val spiderMonitorListeners = ArrayList<SpiderListener>()
                spiderMonitorListeners.add(spiderMonitorListener)
                spider.spiderListeners = spiderMonitorListeners
            } else {
                spider.spiderListeners.add(spiderMonitorListener)
            }

            val spiderStatus = SpiderStatusImpl(spider, spiderMonitorListener)
            this.spiderStatus[spider.uuid] = spiderStatus

            logger.info("Task {} registered to monitor.", spider.uuid)
        }

        return this
    }
}
