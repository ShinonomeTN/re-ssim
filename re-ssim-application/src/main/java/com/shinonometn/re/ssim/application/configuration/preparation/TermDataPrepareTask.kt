package com.shinonometn.re.ssim.application.configuration.preparation

import com.shinonometn.re.ssim.service.data.ImportTaskService
import com.shinonometn.re.ssim.service.courses.CourseInfoService
import com.shinonometn.re.ssim.service.terms.SchoolTermInfoService
import com.shinonometn.re.ssim.service.terms.TermInfoEntity
import com.shinonometn.re.ssim.service.courses.plugin.CourseTermListStore
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.stream.Collectors.toMap

@Component
class TermDataPrepareTask(private val schoolTermInfoService: SchoolTermInfoService,
                          private val importTaskService: ImportTaskService,
                          private val courseInfoService: CourseInfoService,
                          private val courseTermListStore: CourseTermListStore) : ServerInitializeTask {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun order() = 0

    override fun onlyAtFirstTime() = false

    override fun run() {

        prepareTermInfo()

        updateCache()

    }

    private fun prepareTermInfo() {
        val termNames = schoolTermInfoService.queryTermsHasCourses()

        termNames.forEach {
            val schoolTerm = schoolTermInfoService.findByTermName(it.key).orElse(TermInfoEntity())

            // If it is new term, init the data
            if (schoolTerm.name == null) schoolTerm.name = it.key

            // Course count
            schoolTerm.courseCount = it.value

            // Latest version
            schoolTerm.dataVersion = importTaskService.latestVersionOf(schoolTerm.name)

            // Query course types
            courseInfoService.queryTermCourseTypes(schoolTerm.name, schoolTerm.dataVersion).ifPresent {
                schoolTerm.courseTypes = it
            }

            // Week range
            courseInfoService.queryTermWeekRange(schoolTerm.name, schoolTerm.dataVersion).ifPresent {
                schoolTerm.minWeek = it.minimum
                schoolTerm.maxWeek = it.maximum
            }

            schoolTermInfoService.save(schoolTerm)
        }

        logger.info("Database term info was updated.")
    }

    private fun updateCache() {
        courseTermListStore.clear()

        courseTermListStore.putAll(schoolTermInfoService
                .list(Pageable.unpaged())
                .stream()
                .filter { it.name != null && it.courseCount > 0 }
                .collect(toMap({ it.name }, { TermMeta.fromEntity(it) })))

        logger.info("Cache refreshed and initialized.")
    }
}
