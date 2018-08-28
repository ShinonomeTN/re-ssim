package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.services.CourseInfoService
import com.shinonometn.re.ssim.services.LingnanCourseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/server")
class ServerStatusController(private val courseInfoService: CourseInfoService,
                             private val lingnanCourseService: LingnanCourseService) {

    @GetMapping(params = ["hello"])
    fun announce(): Any = HashMap<String, Any>().apply {
        this["ping"] = "pong"

        when{
            courseInfoService.hasData() -> this["db_available"] = true
            lingnanCourseService.capturingTaskCount > 0 -> this["capturing"] = true
            lingnanCourseService.importingTaskCount > 0 -> this["importing"] = true
        }


    }
}