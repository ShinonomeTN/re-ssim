package com.shinonometn.re.ssim.caterpillar.application.controller

import org.apache.http.HttpHeaders
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
class CaterpillarProfileApiTest {

//    @Autowired
//    private lateinit var mockMvc: MockMvc

    @Test
    fun list() {

    }

    @Test
    fun get() {
    }

    @Test
    fun save() {

        // If submit a empty object, api will return a 400 bad request due to form validation
//        mockMvc.perform(
//                post("/profile")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andDo(print())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().`is`(400))

    }

    @Test
    fun getCaterpillarProfileService() {
    }
}