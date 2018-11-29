package com.shinonometn.re.ssim.application.controller

import com.shinonometn.re.ssim.application.security.AuthorityRequired
import com.shinonometn.re.ssim.services.CourseInfoService
import com.shinonometn.re.ssim.services.LingnanCourseService
import com.shinonometn.re.ssim.services.ManagementService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/server")
class ServerStatusController(private val courseInfoService: com.shinonometn.re.ssim.services.CourseInfoService,
                             private val lingnanCourseService: com.shinonometn.re.ssim.services.LingnanCourseService,
                             private val managementService: com.shinonometn.re.ssim.services.ManagementService) {

    @GetMapping(params = ["hello"])
    fun announce(): Any = HashMap<String, Any>().apply {
        this["ping"] = "pong"

        when {
            courseInfoService.hasData() -> this["db_available"] = true
            lingnanCourseService.capturingTaskCount > 0 -> this["capturing"] = true
            lingnanCourseService.importingTaskCount > 0 -> this["importing"] = true
        }
    }

    @GetMapping(params = ["statistics"])
    @AuthorityRequired(name = "statistics:get", group = "Statistics", description = "Show visit statistics.")
    fun statistics(): Any = HashMap<String, Any>().apply {
        this["api_invoke"] = managementService.visitCount
    }
}
