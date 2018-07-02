package com.shinonometn.re.ssim.caterpillar.kingo.capture

import org.jsoup.nodes.Element
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.ResultItems
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor
import java.util.function.Function
import java.util.stream.Collectors

class TermListPageProcessor(private val site: Site) : PageProcessor {

    companion object {
        const val FIELD_TERMS = "terms"

        @JvmStatic
        fun getTerms(resultItems: ResultItems) : Map<String,String> {
            return resultItems.get(FIELD_TERMS)
        }
    }

    override fun process(page: Page) {

        val termList = page.html.document
                .getElementById("form1")
                .select("option")
                .stream()
                .collect(Collectors.toMap<Element,String,String>({ it.attr("value") }, { it.text() }))

        page.putField(FIELD_TERMS, termList)

    }

    override fun getSite(): Site {
        return site
    }

}