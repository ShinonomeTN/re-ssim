package com.shinonometn.re.ssim.service.caterpillar

import us.codecraft.webmagic.Request
import us.codecraft.webmagic.SpiderListener

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * Spider monitor listener
 *
 * This listener will be added to the Spider's listener list
 *
 * It counting and storing base information about tasks of the spider,
 * such as success page count, error page count and so on.
 *
 */
class SpiderMonitorListener : SpiderListener {
    val successCount = AtomicInteger(0)

    val errorCount = AtomicInteger(0)

    private val errorUrls = Collections.synchronizedList(ArrayList<String>())

    override fun onSuccess(request: Request) {
        successCount.incrementAndGet()
    }

    override fun onError(request: Request) {
        errorUrls.add(request.url)
        errorCount.incrementAndGet()
    }

    fun getErrorUrls(): List<String> {
        return errorUrls
    }
}
