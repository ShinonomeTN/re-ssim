package com.shinonometn.re.ssim.application.controller.management.data

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.courses.SchoolCalendarService
import com.shinonometn.re.ssim.service.courses.SchoolTermInfoService
import com.shinonometn.re.ssim.service.courses.entity.SchoolCalendarEntity
import com.shinonometn.re.ssim.service.courses.entity.TermInfo
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/school")
class SchoolTermInfoAPI(private val schoolTermInfoService: SchoolTermInfoService,
                        private val schoolCalendarService: SchoolCalendarService) {

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
                    term = this
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
        schoolCalendarService.pull(schoolTermInfoService.get(termCode) ?: throw BusinessException("term_not_exists"))
        return RexModel.success()
    }

    @PostMapping("/calendar/{termCode}")
    @RequiresPermissions("calendar:write")
    @ApiDescription(title = "Edit a calendar", description = "Edit a calendar")
    fun editCalendar(@RequestBody calendarEntity: SchoolCalendarEntity): SchoolCalendarEntity {
        val calendar = schoolCalendarService.findByTermCode(calendarEntity.term).orElseGet {
            val term = schoolTermInfoService.get(calendarEntity.term)
            SchoolCalendarEntity().apply {
                termName = term.name
            }
        }

        calendar.apply {
            startDate = calendarEntity.startDate
            endDate = calendarEntity.endDate
        }

        return schoolCalendarService.save(calendar)
    }
}
