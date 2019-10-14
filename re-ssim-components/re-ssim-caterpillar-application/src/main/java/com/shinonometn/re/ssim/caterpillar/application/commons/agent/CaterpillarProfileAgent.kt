package com.shinonometn.re.ssim.caterpillar.application.commons.agent

import com.shinonometn.re.ssim.caterpillar.application.commons.TermItem
import us.codecraft.webmagic.Site

abstract class CaterpillarProfileAgent(profileMap: MutableMap<String, Any?>) {

    var agentCode: String by profileMap.withDefault { this.requireAgentCode() }

    var targetDomain: String by profileMap.withDefault { this.requireTargetDomain() }

    var userAgent: String by profileMap.withDefault { "WebMagic/0.7.3 re-ssim/2.0 (Lingnan College) AgentCode/${agentCode} (CaterpillarProfileAgent/Kotlin.1.2.41)" }

    var timeoutMillis: Int by profileMap.withDefault { 5000 }

    var retryTimes : Int by profileMap.withDefault { 81 }

    var sleepTimes : Int by profileMap.withDefault { 500 }

    /**
     * provide the default target domain
     */
    protected abstract fun requireTargetDomain(): String

    /**
     * provide the agent code of this agent
     *
     * agent code is for identifying each caterpillar function set,
     * the service application will use the right program to handle
     * the fetching according to agent code.
     */
    protected abstract fun requireAgentCode(): String

    /**
     * provide the default spider site settings
     */
    protected open fun createSite() = Site
            .me()
            .setDomain(this.targetDomain)
            .setTimeOut(timeoutMillis)
            .setRetryTimes(retryTimes)
            .setSleepTime(sleepTimes)
            .setUserAgent(userAgent)

    /**
     * Fetch term list from remote
     */
    abstract fun fetchTerms() : Collection<TermItem>
}