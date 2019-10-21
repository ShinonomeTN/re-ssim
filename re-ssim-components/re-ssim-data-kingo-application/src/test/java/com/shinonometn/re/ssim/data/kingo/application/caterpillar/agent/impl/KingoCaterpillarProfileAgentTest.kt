package com.shinonometn.re.ssim.data.kingo.application.caterpillar.agent.impl

import com.shinonometn.re.ssim.commons.JSON
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.agent.KingoCaterpillarProfileAgent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class KingoCaterpillarProfileAgentTest {

    @Test
    fun testGetPropertiesFromEmptyBean() {
        KingoCaterpillarProfileAgent(HashMap()).run {
            assertNotNull(agentCode)
            assertNotNull(targetDomain)
            assertNotNull(userAgent)
            assertNotNull(timeoutMillis)
            assertNotNull(retryTimes)
            assertNotNull(sleepTimes)
            assertNotNull(taskThreads)

            assertEquals(username, null)
            assertEquals(password, null)
            assertEquals(role, "STU")
            assertEquals(encoding, "utf8")
        }
    }

    @Test
    fun testGetPropertiesFromExistsBean() {
        //language=JSON
        KingoCaterpillarProfileAgent(JSON.readAsMap("""
            {
                "agentCode":null,
                "targetDomain":null,
                "userAgent":null,
                "timeoutMillis":null,
                "retryTimes":null,
                "sleepTimes":null,
                "taskThreads":null,
                "username":"",
                "password":"",
                "role":"STU",
                "encoding":"gbk2312"
            }
        """.trimIndent())).run {
            //language=TEXT
            assertNotNull(agentCode)
            assertNotNull(targetDomain)
            assertNotNull(userAgent)
            assertNotNull(timeoutMillis)
            assertNotNull(retryTimes)
            assertNotNull(sleepTimes)
            assertNotNull(taskThreads)

            assertEquals(username, "")
            assertEquals(password, "")
            assertEquals(role, "STU")
            assertEquals(encoding, "gbk2312")
        }
    }

}