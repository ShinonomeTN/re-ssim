package com.shinonometn.re.ssim.caterpillar.kingo.capture

import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor

class LoginStatusPageProcessor(private val site: Site) : PageProcessor {

    override fun process(page: Page) {

        val element = page.html.document.getElementById("txt_yzm")
        page.putField("isLogin", element.parent().attr("style") == "display:none")

    }

    override fun getSite(): Site {
        return site
    }
}
