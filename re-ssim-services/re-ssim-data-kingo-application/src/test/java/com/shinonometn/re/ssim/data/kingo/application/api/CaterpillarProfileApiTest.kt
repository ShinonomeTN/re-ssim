package com.shinonometn.re.ssim.data.kingo.application.api

import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class CaterpillarProfileApiTest {

    @Autowired
    private lateinit var mock: MockMvc

    @Test
    fun list() {
        mock.perform(get("/profile")).andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.size").value(equalTo(10)))
                .andExpect(jsonPath("$.number").value(equalTo(0)))
                .andExpect(jsonPath("$.content").isArray)
    }

    @Test
    fun get() {
        mock.perform(get("/profile/{id}", 1))
                .andDo(print())
    }

    @Test
    fun save() {

        // If submit a empty object, api will return a 400 bad request due to form validation
        mock.perform(
                post("/profile").contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().`is`(400))

    }

    @Test
    fun validate() {
        mock.perform(get("/profile/{id}", 1).param("validate", ""))
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    fun findByUser() {
        mock.perform(get("/profile").param("owner", "lan"))
                .andDo(print())
                .andExpect(jsonPath("$.size").value(equalTo(10)))
                .andExpect(jsonPath("$.number").value(equalTo(0)))
                .andExpect(jsonPath("$.content").isArray)
    }

    @Test
    fun getByUserAndProfileName() {
        mock.perform(get("/profile").param("profile_name", "cat").param("owner", "lan"))
                .andDo(print())
                .andExpect(status().isOk)
    }
}