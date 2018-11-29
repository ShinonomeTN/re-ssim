package com.shinonometn.re.ssim.service.caterpillar

import java.util.Date

interface SpiderStatus {
    val name: String

    val status: String

    val thread: Int

    val totalPageCount: Int

    val leftPageCount: Int

    val successPageCount: Int

    val errorPageCount: Int

    val errorPages: List<String>

    val startTime: Date

    val pagePerSecond: Int

    fun start()

    fun stop()
}
