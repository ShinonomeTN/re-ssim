package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import com.shinonometn.re.ssim.data.kingo.application.service.TermDataService
import com.shinonometn.re.ssim.data.kingo.application.service.TermManageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/term")
class TermApi(private val termManageService: TermManageService,
              private val termDataService: TermDataService) {

    @GetMapping
    fun list(@PageableDefault pageable: Pageable): Page<TermInfo> = termManageService.list(pageable)

    @PostMapping("/{id}/calendar", params = ["refresh", "profileId"])
    fun refreshCalendar(@PathVariable("id") id: Int, @RequestParam("profileId") profileId: Int): TermInfo {
        val termInfo = termManageService
                .find(id)
                .orElseThrow { BusinessException("term_not_found") }

        return termManageService.refreshTermCalendarInfo(termInfo.identity!!, profileId)
    }

    @GetMapping("/{termCode}")
    fun get(@PathVariable("termCode") termCode: String): Optional<Map<String, Any?>> = termManageService
            .findByTermCode(termCode).map { termInfo ->
                HashMap<String, Any?>().apply {
                    this["termInfo"] = termInfo

                    if (!StringUtils.isEmpty(termInfo.dataVersion)) {
                        listTermCourse(termCode, termInfo.dataVersion!!).ifPresent {
                            this["courses"] = it
                        }

                        listTermClasses(termCode, termInfo.dataVersion!!).ifPresent {
                            this["classes"] = it
                        }

                        listTermClassrooms(termCode, termInfo.dataVersion!!).ifPresent {
                            this["classrooms"] = it
                        }

                        listTermTeachers(termCode, termInfo.dataVersion!!).ifPresent {
                            this["teachers"] = it
                        }

                        listTermDepartments(termCode, termInfo.dataVersion!!).ifPresent {
                            this["departments"] = it
                        }
                    }
                }
            }

    /**
     *
     * List termName courses
     *
     */
    @GetMapping("/{termCode}/course")
    fun listTermCourse(@PathVariable("termCode") termCode: String,
                       @RequestParam("data_version") dataVersion: String) =
            termDataService.listAllCoursesOfTerm(termCode, dataVersion)

    /**
     *
     * List all teacher presented in termName
     *
     */
    @GetMapping("/{name}/teacher")
    fun listTermTeachers(@PathVariable("name") termName: String,
                         @RequestParam("data_version") dataVersion: String) =
            termDataService.listTeachersOfTerm(termName, dataVersion)

    /**
     *
     * List all termName class
     *
     */
    @GetMapping("/{name}/class")
    fun listTermClasses(@PathVariable("name") termName: String,
                        @RequestParam("data_version") dataVersion: String) =
            termDataService.listClassesOfTerm(termName, dataVersion)


    /**
     *
     * List all classrooms that used in termName
     *
     */
    @GetMapping("/{name}/classroom")
    fun listTermClassrooms(@PathVariable("name") termName: String,
                           @RequestParam("data_version") dataVersion: String) =
            termDataService.listClassroomsOfTerm(termName, dataVersion)

    /*
    *
    * List all department presented in this term
    *
    * */
    @GetMapping("/{name}/department")
    fun listTermDepartments(@PathVariable("name") termCode: String,
                            @RequestParam("data_version") dataVersion: String) =
            termDataService.listDepartmentsOfTerm(termCode, dataVersion)
}
