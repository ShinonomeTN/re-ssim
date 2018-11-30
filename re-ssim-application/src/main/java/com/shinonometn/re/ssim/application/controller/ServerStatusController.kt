package com.shinonometn.re.ssim.application.controller

import com.shinonometn.re.ssim.service.caterpillar.CaterpillarTaskService
import com.shinonometn.re.ssim.service.courses.CourseInfoService
import com.shinonometn.re.ssim.service.statistics.StatisticsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/server")
class ServerStatusController(private val courseInfoService: CourseInfoService,
                             private val statisticsService: StatisticsService,
                             private val caterpillarTaskService: CaterpillarTaskService) {

    @GetMapping(params = ["hello"])
    fun announce(): Any = HashMap<String, Any>().apply {
        this["ping"] = "pong"

        when {
            courseInfoService.hasData() -> this["db_available"] = true
            caterpillarTaskService.capturingTaskCount > 0 -> this["capturing"] = true
            caterpillarTaskService.importingTaskCount > 0 -> this["importing"] = true
        }
    }

//    @GetMapping(params = ["statistics"])
//    @AuthorityRequired(name = "statistics:get", group = "Statistics", description = "Show visit statistics.")
//    fun statistics(): Any = HashMap<String, Any>().apply {
//        this["api_invoke"] = managementService.visitCount
//    }
}
