package com.shinonometn.re.ssim.service.caterpillar.kingo.capture

import us.codecraft.webmagic.Page
import us.codecraft.webmagic.ResultItems
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor

class LoginStatusPageProcessor(private val site: Site) : PageProcessor {

    companion object {

        private const val FIELD_IS_LOGIN = "isLogin"

        @JvmStatic
        fun getIsLogin(resultItems: ResultItems) : Boolean {
            return resultItems[FIELD_IS_LOGIN]
        }
    }

    override fun process(page: Page) {
        val element = page.html.document.getElementById("txt_yzm")
        page.putField(
                FIELD_IS_LOGIN,
                element.parent().attr("style") == "display:none")
    }

    override fun getSite(): Site {
        return site
    }
}
