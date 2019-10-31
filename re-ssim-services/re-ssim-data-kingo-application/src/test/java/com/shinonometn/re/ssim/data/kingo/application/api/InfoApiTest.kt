package com.shinonometn.re.ssim.data.kingo.application.api

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class InfoApiTest {

    @Autowired
    private lateinit var mock: MockMvc

    @Test
    fun listAllCachedTerms() {
        mock.perform(get("/info/remote_term"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun updateCachedTermList() {
        mock.perform(get("/info/remote_term")
                .param("refresh").param("profile_id", "3"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)

    }
}