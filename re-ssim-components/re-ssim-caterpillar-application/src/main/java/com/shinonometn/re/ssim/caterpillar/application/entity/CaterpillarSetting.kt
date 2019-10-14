package com.shinonometn.re.ssim.caterpillar.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.utils.JsonMapAttributeConverter
import org.springframework.data.annotation.Id
import us.codecraft.webmagic.Site
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@JsonInclude(JsonInclude.Include.NON_NULL)
class CaterpillarSetting : Serializable {

    /*
        Base info
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
    @Column(columnDefinition = "VARCHAR(64)")
    var owner: String? = null
    @Column(columnDefinition = "VARCHAR(64)")
    var name: String? = null
    var description: String? = null

    /*
        Caterpillar profile details
     */

    @Convert(converter = JsonMapAttributeConverter::class)
    @Column(columnDefinition = "TEXT")
    var caterpillarProfile: MutableMap<String, Any?>? = null

    /*
        Caterpillar options
     */
    var threads: Int = 1

//    fun createSite(): Site = Site
//            .me()
//            .setDomain("jwgl.lnc.edu.cn")
//            .setTimeOut(5000)
//            .setRetryTimes(81)
//            .setSleepTime(500)
//            .setUserAgent(userAgent)
//            .setCharset(encoding)

//    companion object {
//        fun createDefaultSite(): Site = Site
//                .me()
//                .setDomain("jwgl.lnc.edu.cn")
//                .setTimeOut(5000)
//                .setRetryTimes(81)
//                .setSleepTime(500)
//                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
//                .setCharset("GBK")
//
//    }
}
