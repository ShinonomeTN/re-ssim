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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class CourseQueryAPITest {

    @Autowired
    private lateinit var mock: MockMvc

    @Test
    fun showClassTermWeeks() {
        mock.perform(get("/term/{term}/class/{clazzName}/week", "20181", "17软件技术2班")
                .param("data_version", "1571668181856695816"))
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    fun queryClassWeekCourses() {
        mock.perform(get("/term/{term}/class/{class}/course", "20181", "17软件技术2班")
                .param("week", "1")
                .param("excludedType", "")
                .param("data_version", "1571668181856695816"))
                .andDo(print())
                .andExpect(status().isOk)

    }

    @Test
    fun showTeacherTermWeeks() {
        mock.perform(get("/term/{term}/teacher/{teacher}/week", "20181", "吴道君")
                .param("data_version", "1571668181856695816"))
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    fun queryTeacherWeekCourses() {
        mock.perform(get("/term/{term}/teacher/{teacher}/course", "20181", "吴道君")
                .param("data_version", "1571668181856695816")
                .param("week", "1"))
                .andDo(print())
                .andExpect(status().isOk)
    }
}