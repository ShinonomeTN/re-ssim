package com.shinonometn.re.ssim.controller.management

import com.shinonometn.re.ssim.commons.session.HttpSessionWrapper
import com.shinonometn.re.ssim.data.caterpillar.CaptureTaskDTO
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSettingRepository
import com.shinonometn.re.ssim.security.AuthorityRequired
import com.shinonometn.re.ssim.services.LingnanCourseService
import com.shinonometn.re.ssim.services.ManagementService
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/api/mng")
open class DataManagementController(private val lingnanCourseService: LingnanCourseService,
                                    private val managementService: ManagementService,
                                    private val cacheManager: CacheManager,
                                    private val caterpillarSettingRepository: CaterpillarSettingRepository) {

    /**
     *
     * Get term list
     *
     */
    @GetMapping("/term")
    @ResponseBody
    @AuthorityRequired(name = "term:get", group = "School Terms", description = "Get school terms, if not being cached, fetch from remote.")
    open fun termList(): Map<String, Any> = lingnanCourseService.termList

    /**
     *
     * Clear cache and get term list
     *
     */
    @GetMapping("/term", params = ["refresh"])
    @ResponseBody
    @AuthorityRequired(name = "term:refresh", group = "School Terms", description = "Get school terms from remote and evict the cache")
    open fun termListRefresh(): Map<String, Any> = lingnanCourseService.reloadAndGetTermList()

    /**
     *
     * List all tasks
     *
     */
    @GetMapping("/task")
    @ResponseBody
    @AuthorityRequired(name = "task:get", group = "Capture Tasks", description = "List all capture tasks.")
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
    @AuthorityRequired(name = "task:create", group = "Capture Tasks", description = "Create a capture task.")
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
    @AuthorityRequired(name = "task:fire", group = "Capture Tasks", description = "Start a capture task.")
    open fun startTask(@PathVariable("id") id: String, @RequestParam("profile") profileName: String, session: HttpSession): Any {
        val sessionWrapper = HttpSessionWrapper(session)

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
    open fun importTask(@PathVariable("id") id: String) =
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
    open fun clearCache() =
            cacheManager.cacheNames
                    .map { cacheManager.getCache(it) }
                    .filter { it != null }
                    .forEach { it?.clear() }
}
