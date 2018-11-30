package com.shinonometn.re.ssim.service.caterpillar.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import us.codecraft.webmagic.Site

@JsonInclude(JsonInclude.Include.NON_NULL)
class CaterpillarSetting {

    /*
        Base info
     */
    @Id
    var id: String? = null
    var owner: String? = null
    var name: String? = null
    var description: String? = null

    /*
        Login info
     */
    var username: String? = null
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null
    var role: String? = null

    /*
        Caterpillar options
     */
    var userAgent: String? = null
    var encoding: String? = "utf8"
    var threads: Int = 1

    fun createSite(): Site = Site
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
        fun createDefaultSite(): Site = Site
                .me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(5000)
                .setRetryTimes(81)
                .setSleepTime(500)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .setCharset("GBK")

    }
}
