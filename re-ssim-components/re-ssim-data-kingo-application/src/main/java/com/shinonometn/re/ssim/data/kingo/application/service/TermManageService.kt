package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.base.calendar.SchoolDate
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.KingoSchoolCalendar
import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import com.shinonometn.re.ssim.data.kingo.application.repository.TermInfoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class TermManageService(private val termInfoRepository: TermInfoRepository,
                        private val termDataService: TermDataService,
                        private val caterpillarService: CaterpillarService,
                        private val caterpillarSettingsService: CaterpillarSettingsService) {

    private val logger = LoggerFactory.getLogger("ressim.kingo.term_manage_service")

    fun refreshTermInfo(termCode: String, termName: String, useDataVersion: String): TermInfo = termInfoRepository
            .findByIdentity(termCode)
            .orElse(TermInfo().apply {
                this.identity = termCode
                this.name = termName

                this.dataVersion = useDataVersion

                this.createDate = Date()
            }).apply {
                updateDate = Date()

                courseCount = termDataService.countTermCourses(identity!!, dataVersion!!).orElse(0)
                courseTypes = termDataService.listAllCourseTypesOfTerm(identity!!, dataVersion!!).orElse(ArrayList()) as MutableList<String>

                termDataService.getWeekRangeOfTerm(identity!!, dataVersion!!).ifPresent {
                    maxWeek = it.maximum
                    minWeek = it.minimum
                }
            }.run {
                logger.debug("Term info updated. {}", this)
                return termInfoRepository.save(this)
            }

    fun refreshTermCalendarInfo(termCode: String, profileId: Int): TermInfo {
        val termInfo = termInfoRepository.findByIdentity(termCode).orElseThrow { BusinessException("term_not_found") }

        val caterpillarSetting = caterpillarSettingsService
                .findById(profileId)
                .map { it.caterpillarProfile!! }
                .orElseThrow { BusinessException("caterpillar_settings_not_found") }

        val calendarInfo = caterpillarService.getAgent(caterpillarSetting)
                .fetchCalendarForTerm(termCode)
                .stream()
                .filter { it.termName == termInfo.name }
                .findFirst()
                .orElseThrow {
                    BusinessException("no_remote_calendar_result")
                }

        termInfo.startDate = calendarInfo.startDate
        termInfo.endDate = calendarInfo.endDate

        logger.debug("Term calendar updated. {}", termInfo)

        return termInfoRepository.save(termInfo)
    }

    fun queryLatestSchoolCalendar(): List<SchoolDate> {
        val current = Calendar.getInstance().time
        val now = LocalDateTime.ofInstant(current.toInstant(), ZoneId.systemDefault())

        return termInfoRepository
                .findByDateAfter(current)
                .map { KingoSchoolCalendar(it.name, it.startDate, it.endDate).getFromDateTime(now) }
    }

    fun save(termInfo: TermInfo): TermInfo = termInfoRepository.save(termInfo)
    fun find(id: Int): Optional<TermInfo> = termInfoRepository.findById(id)
    fun list(pageable: Pageable): Page<TermInfo> = termInfoRepository.findAll(pageable)

    fun findByTermCode(termCode : String) : Optional<TermInfo> = termInfoRepository.findByIdentity(termCode)
}
