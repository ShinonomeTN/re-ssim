package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.models.CaptureTaskDTO
import com.shinonometn.re.ssim.services.LingnanCourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

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
    fun taskList(): List<CaptureTaskDTO> {
        return lingnanCourseService.listTasks()
    }

    @PostMapping("/task")
    @ResponseBody
    fun createTask(@RequestParam("termCode") termCode: String) =
            HashMap<String, Any>().apply {
                if (!lingnanCourseService.termList!!.containsKey(termCode)) {
                    this["message"] = "unknown_term_code"
                    this["error"] = "task_create_failed"
                } else {
                    this["message"] = "success"
                    this["data"] = lingnanCourseService.createTask(termCode)
                }
            }

    @PostMapping("/task/{id}", params = ["start"])
    @ResponseBody
    fun startTask(@PathVariable("id") id: String): CaptureTaskDTO? =
            lingnanCourseService.startTask(id)

    @PostMapping("/task/{id}", params = ["stop"])
    fun stopTask(@PathVariable("id") id: String) = HashMap<String, Any>().apply {
        val dto = lingnanCourseService.stopTask(id)
        if (dto == null) {
            this["error"] = "task_operate_failed"
            this["message"] = "task_not_registered"
        } else {
            this["message"] = "success"
            this["data"] = dto
        }
    }

}
