package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.models.CaptureTask
import com.shinonometn.re.ssim.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.services.LingnanCourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api/mng")
class ManagementController(@Autowired private val lingnanCourseService: LingnanCourseService) {

    @GetMapping("/termList")
    @ResponseBody
    fun termList(): MutableMap<String, String>? = lingnanCourseService.termList

    @GetMapping("/termList", params = ["refresh"])
    @ResponseBody
    fun termListRefresh(): MutableMap<String, String>? = lingnanCourseService.reloadAndGetTermList()

    @GetMapping("/task")
    @ResponseBody
    fun taskList() : MutableIterable<CaptureTask>? {

    }
}
