package com.shinonometn.re.ssim.data.caterpillar

import org.springframework.data.annotation.Id
import us.codecraft.webmagic.Site

class CaterpillarSetting {

    @Id
    var id: String? = null
    var userId: String? = null

    var username: String? = null
    var password: String? = null
    var role: String? = null
    var userAgent: String? = null
    var encoding: String? = null
    var threads: Int = 1

    fun createSite() : Site = Site
            .me()
            .setDomain("jwgl.lnc.edu.cn")
            .setTimeOut(5000)
            .setRetryTimes(81)
            .setSleepTime(500)
            .setUserAgent(userAgent)
            .setCharset(encoding)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CaterpillarSetting

        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        return username?.hashCode() ?: 0
    }

    companion object {
        fun createDefaultSite() : Site = Site
                .me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(5000)
                .setRetryTimes(81)
                .setSleepTime(500)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .setCharset("GBK")

    }
}