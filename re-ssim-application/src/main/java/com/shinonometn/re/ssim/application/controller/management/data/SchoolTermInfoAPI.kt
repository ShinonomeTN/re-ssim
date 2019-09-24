package com.shinonometn.re.ssim.application.controller.management.data

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.service.calendar.SchoolCalendarService
import com.shinonometn.re.ssim.service.terms.SchoolTermInfoService
import com.shinonometn.re.ssim.service.calendar.SchoolCalendarEntity
import com.shinonometn.re.ssim.service.terms.TermInfo
import com.shinonometn.re.ssim.service.courses.plugin.CourseTermListStore
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/school")
class SchoolTermInfoAPI(private val schoolTermInfoService: SchoolTermInfoService,
                        private val schoolCalendarService: SchoolCalendarService,
                        private val validator: Validator,
                        private val courseTermListStore: CourseTermListStore) {

    /*
    *
    * Terms
    *
    * */

    @GetMapping("/term")
    @RequiresPermissions("term:read")
    @ApiDescription(title = "Get all terms", description = "Get all saved term info, those info will be update after some capture actions")
    fun listTerms(@PageableDefault pageable: Pageable): Page<TermInfo> =
            schoolTermInfoService.list(pageable).map {
                TermInfo().apply {
                    term = it
                    hasLocalCourseData = courseTermListStore.contains(it.name)
                    hasLocalCalendarData = schoolCalendarService.existsByTermName(it.name)
                }
            }

    @PostMapping("/term", params = ["pull"])
    @RequiresPermissions("term:pull")
    @ApiDescription(title = "Pull term info", description = "Pull all terms, update exists term info")
    fun pullTerms(): RexModel<Any> {
        schoolTermInfoService.pull()
        return RexModel.success()
    }

    /*
    *
    * Calendars
    *
    *
    * */

    @GetMapping("/calendar")
    @RequiresPermissions("calendar:read")
    @ApiDescription(title = "List all local calendars", description = "List all local calendars")
    fun listCalendars(@PageableDefault pageable: Pageable): Page<SchoolCalendarEntity> = schoolCalendarService.list(pageable)

    @PostMapping("/calendar/{termCode}", params = ["pull"])
    @RequiresPermissions("calendar:update")
    @ApiDescription(title = "Update a calendar", description = "Update the calendar belong to a term")
    fun pullCalendar(@PathVariable("termCode") termCode: String): RexModel<Any> {
        schoolCalendarService.pull(schoolTermInfoService.findByTermCode(termCode).orElseThrow { BusinessException("term_not_exists") })
        return RexModel.success()
    }

    @PostMapping("/calendar")
    @RequiresPermissions("calendar:write")
    @ApiDescription(title = "Edit a calendar", description = "Edit a calendar")
    fun editCalendar(@RequestBody form: SchoolCalendarEntity): SchoolCalendarEntity {

        validator.validate(form)

        // Find an exists calendar or create a new one
        val calendar = schoolCalendarService.findById(form.id).orElseGet {
            SchoolCalendarEntity().apply {
                schoolTermInfoService.findByTermName(form.termName).ifPresent {
                    this.termName = it.name
                    this.term = it.code
                }
            }
        }

        if (form.term != null) calendar.term = form.term
        if (form.termName != null) calendar.termName = form.termName

        BeanUtils.copyProperties(form, calendar, "id", "createTime", "term", "termName")

        return schoolCalendarService.save(calendar)
    }
}
