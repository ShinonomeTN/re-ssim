package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTaskDTO
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/api/mng")
open class DataManagementController(private val lingnanCourseService: LingnanCourseServicey) {

    /**
     *
     * Get term list
     *
     */
    @GetMapping("/term")
    @ResponseBody
    @ApiDescription(title = "Get school term list", description = "Get school terms, if not being cached, fetch from remote.")
    @RequiresPermissions("term_list:read")
    open fun termList(): Map<String, Any> = lingnanCourseService.termList

    /**
     *
     * Clear cache and get term list
     *
     */
    @GetMapping("/term", params = ["refresh"])
    @ResponseBody
    @ApiDescription(title = "Refresh school term list", description = "Get school terms from remote and evict the cache")
    @RequiresPermissions("term_list:refresh")
    open fun termListRefresh(): Map<String, Any> = lingnanCourseService.reloadAndGetTermList()

    /**
     *
     * List all tasks
     *
     */
    @GetMapping("/task")
    @ResponseBody
    @ApiDescription(title = "List all capture tasks", description = "List all capture tasks. If task is running, show progress")
    @RequiresPermissions("task:list")
    open fun taskList(): List<CaptureTaskDTO> {
        return lingnanCourseService.listTasks()
    }

    /**
     *
     * create capture task
     *
     */
    @PostMapping("/task")
    @ResponseBody
    @ApiDescription(title = "Create a task", description = "Create a capture task.")
    @RequiresPermissions("task:create")
    open fun createTask(@RequestParam("termCode") termCode: String) =
            HashMap<String, Any>().apply {
                val termList = lingnanCourseService.termList
                if (!termList.containsKey(termCode)) {
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
    @ApiDescription(title = "Start a capture task", description = "Start a capture task.")
    @RequiresPermissions("task:start")
    open fun startTask(@PathVariable("id") id: String, @RequestParam("profile") profileName: String, session: HttpSession): Any {
        val sessionWrapper = com.shinonometn.re.ssim.commons.session.HttpSessionWrapper(session)

        val user = managementService.getUser(sessionWrapper.userDetails.username)!!

        return lingnanCourseService.startTask(
                id,
                caterpillarSettingRepository.findByUserAndUsername(user.id, profileName) ?: return HashMap<String, Any>().apply {
                    this["error"] = "start_task_failed"
                    this["message"] = "could_not_get_profile"
                })

    }

    /**
     *
     * Stop a capture task
     *
     */
    @AuthorityRequired(name = "task:stop", group = "Capture Tasks", description = "Stop a capture task.")
    @PostMapping("/task/{id}", params = ["stop"])
    @ResponseBody
    @RequiresPermissions("task:stop")
    open fun stopTask(@PathVariable("id") id: String) =
            HashMap<String, Any>().apply {
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
     * Resume task
     *
     */
    @PostMapping("/task/{id}", params = ["resume"])
    @ResponseBody
    @AuthorityRequired(name = "task:resume", group = "Capture Tasks", description = "Resume a capture task.")
    @RequiresPermissions("task:restart")
    open fun resumeTask(@PathVariable("id") id: String) =
            HashMap<String, Any>().apply {
                this["message"] = "success"
                this["data"] = lingnanCourseService.resumeTask(id)
            }

    /**
     *
     * Start import task data to DB
     *
     */
    @PostMapping("/task/{id}", params = ["import"])
    @ResponseBody
    @AuthorityRequired(name = "task:import", group = "Capture Tasks", description = "Import a finshed capture task.")
    @RequiresPermissions("task:import")
    open fun importTask(@PathVariable("id") id: String) =
            HashMap<String, Any>().apply {

                val captureTaskDTO: com.shinonometn.re.ssim.data.caterpillar.CaptureTaskDTO? = lingnanCourseService.queryTask(id)

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
                this["data"] = lingnanCourseService.importSubjectData(captureTaskDTO)
            }

    /**
     *
     * Delete a task
     *
     */
    @DeleteMapping("/task/{id}")
    @ResponseBody
    @AuthorityRequired(name = "task:delete", group = "Capture Tasks", description = "Delete a capture task.")
    @RequiresPermissions("task:delete")
    open fun deleteTask(@PathVariable("id") id: String) =
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
    @AuthorityRequired(name = "task.dash:get", group = "Capture Tasks", description = "Get task status.")
    @RequiresPermissions("dash.task:get")
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
    @AuthorityRequired(name = "cache:clear", group = "Cache Management", description = "Clear all caches.")
    @PostMapping("/cache", params = ["clear"])
    @ResponseBody
    @RequiresPermissions("cache:delete")
    open fun clearCache() =
            cacheManager.cacheNames
                    .map { cacheManager.getCache(it) }
                    .filter { it != null }
                    .forEach { it?.clear() }
}
