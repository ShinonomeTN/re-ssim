package com.shinonometn.re.ssim.caterpillar.application.commons.agent.impl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.commons.TermItem
import com.shinonometn.re.ssim.caterpillar.application.commons.agent.CaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.TermListPageProcessor
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import java.util.*
import java.util.stream.Collectors

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class KingoCaterpillarProfileAgent(private val map: MutableMap<String, Any?>) : CaterpillarProfileAgent(map) {

    var username: String? by map
    var password: String? by map
    var role: String? by map

    var encoding: String by map.withDefault { "utf8" }

    @JsonIgnore
    override fun requireTargetDomain(): String = "jwgl.lnc.edu.cn"

    override fun requireAgentCode() = "kingo.lingnan_college"

    override fun createSite() : Site = super.createSite().setCharset(encoding)

    override fun fetchTerms(): Collection<TermItem> {

        val result = LinkedList<TermItem>()

        Spider.create(TermListPageProcessor(createSite()))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline { r, _ ->
                    result.addAll(
                            TermListPageProcessor
                                    .getTerms(r)
                                    .entries
                                    .stream()
                                    .map { TermItem(it.key, it.value) }
                                    .collect(Collectors.toList())
                    )
                }
                .run()

        return result;
    }
}