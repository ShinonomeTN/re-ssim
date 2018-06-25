package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.commons.CacheKeys
import com.shinonometn.re.ssim.models.CaptureTaskDTO
import com.shinonometn.re.ssim.services.LingnanCourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File

@Controller
@RequestMapping("/api/mng")
open class ManagementController(@Autowired private val lingnanCourseService: LingnanCourseService) {

    /**
     *
     * Get term list
     *
     */
    @GetMapping("/terms")
    @ResponseBody
    fun termList(): MutableMap<String, String>? = lingnanCourseService.termList

    /**
     *
     * Clear cache and get term list
     *
     */
    @GetMapping("/terms", params = ["refresh"])
    @ResponseBody
    fun termListRefresh(): MutableMap<String, String>? = lingnanCourseService.reloadAndGetTermList()

    /**
     *
     * List all tasks
     *
     */
    @GetMapping("/task")
    @ResponseBody
    fun taskList(): List<CaptureTaskDTO> {
        return lingnanCourseService.listTasks()
    }

    /**
     *
     * create capture task
     *
     */
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

    /**
     *
     * Start a task
     *
     */
    @PostMapping("/task/{id}", params = ["start"])
    @ResponseBody
    fun startTask(@PathVariable("id") id: String): CaptureTaskDTO? =
            lingnanCourseService.startTask(id)

    /**
     *
     * Stop a capture task
     *
     */
    @PostMapping("/task/{id}", params = ["stop"])
    @ResponseBody
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

    /**
     *
     * Start import task data to DB
     *
     */
    @PostMapping("/task/{id}", params = ["import"])
    @ResponseBody
    fun importTask(@PathVariable("id") id: String) =
            HashMap<String, Any>().apply {

                val captureTaskDTO: CaptureTaskDTO? = lingnanCourseService.queryTask(id)

                when {
                    captureTaskDTO == null -> {
                        this["error"] = "task_import_failed"
                        this["message"] = "task_not_found"
                        return this
                    }

                    captureTaskDTO.finished -> {
                        this["error"] = "task_import_failed"
                        this["message"] = "task_finished"
                        return this
                    }

                    !captureTaskDTO.folderExist -> {
                        this["error"] = "task_import_failed"
                        this["message"] = "capture_directory_not_found"
                        return this
                    }

                    captureTaskDTO.spiderStatus != null && captureTaskDTO.spiderStatus!!.status == "Running" -> {
                        this["error"] = "task_import_failed"
                        this["spider_running"] = "spider_running"
                        return this
                    }
                }

                this["message"] = "success"
                this["data"] = lingnanCourseService.importSubjectData(captureTaskDTO);
            }

    /**
     *
     * Delete a task
     *
     */
    @DeleteMapping("/task/{id}")
    @ResponseBody
    fun deleteTask(@PathVariable("id") id: String) =
            HashMap<String, Any>().apply {
                val captureTaskResult = lingnanCourseService.queryTask(id)
                if (captureTaskResult == null) {
                    this["error"] = "delete_task_failed"
                    this["message"] = "task_not_found"
                    return this
                }

                if (captureTaskResult.folderExist) File(captureTaskResult.tempDir).deleteRecursively()
                lingnanCourseService.deleteTask(id)
                this["message"] = "success"
            }

    /**
     *
     * Get current running task counts
     *
     */
    @GetMapping("/dashes")
    @ResponseBody
    fun dashes() =
            HashMap<String, Any>().apply {
                this["importingTaskCount"] = lingnanCourseService.importingTaskCount
                this["capturingTaskCount"] = lingnanCourseService.capturingTaskCount
            }

    /**
     *
     * Force clear all cache
     *
     */
    @PostMapping("/cache", params = ["clear"])
    @ResponseBody
    @CacheEvict(
            CacheKeys.CAPTURE_TERM_LIST,
            CacheKeys.TERM_LIST,
            CacheKeys.TERM_TEACHER_LIST,
            CacheKeys.TERM_CLASS_LIST,
            CacheKeys.TERM_COURSE_LIST,
            CacheKeys.TERM_WEEK_RANGE,
            allEntries = true)
    open fun clearCache() =
            HashMap<String, Any>().apply {
                this["message"] = "success"
            }
}
