package com.shinonometn.re.ssim.application.controller.course

import com.shinonometn.re.ssim.commons.CacheKeys
import com.shinonometn.re.ssim.service.courses.CourseInfoService
import com.shinonometn.re.ssim.service.courses.SchoolTermInfoService
import com.shinonometn.re.ssim.service.courses.plugin.CourseTermListStore
import com.shinonometn.re.ssim.service.courses.plugin.SchoolCalendarStore
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/term")
open class TermInfoAPI(private val courseInfoService: CourseInfoService,
                       private val termInfoService: SchoolTermInfoService,
                       private val courseTermListStore: CourseTermListStore,
                       private val schoolCalendarStore: SchoolCalendarStore) {

    /**
     *
     * List all terms
     *
     */
    @GetMapping
    open fun list(): Collection<TermMeta>? =
            courseTermListStore.all

    /**
     *
     * List termName courses
     *
     */
    @GetMapping("/{name}/course")
    @Cacheable(CacheKeys.TERM_COURSE_LIST)
    open fun listTermCourse(@PathVariable("name") termName: String): Any =
            courseInfoService.queryTermCourse(termName, courseTermListStore.getTermMeta(termName).dataVersion)

    /**
     *
     * List all teacher presented in termName
     *
     */
    @GetMapping("/{name}/teacher")
    @Cacheable(CacheKeys.TERM_TEACHER_LIST)
    open fun listTermTeachers(@PathVariable("name") termName: String): Any? =
            courseInfoService.queryTermTeachers(termName, courseTermListStore.getTermMeta(termName).dataVersion)

    /**
     *
     * List all termName class
     *
     */
    @GetMapping("/{name}/class")
    @Cacheable(CacheKeys.TERM_CLASS_LIST)
    open fun listTermClasses(@PathVariable("name") termName: String): Any? =
            courseInfoService.queryTermClasses(termName, courseTermListStore.getTermMeta(termName).dataVersion)

    /**
     *
     * List all classrooms that used in termName
     *
     */
    @GetMapping("/{name}/classroom")
    @Cacheable(CacheKeys.TERM_CLASSROOM)
    open fun listTermClassrooms(@PathVariable("name") termName: String): Any? =
            courseInfoService.queryTermClassrooms(termName, courseTermListStore.getTermMeta(termName).dataVersion)

    /**
     *
     * Get current school calendar
     *
     * */
    @GetMapping("/calendar")
    open fun currentTerm(): Any? {
        return schoolCalendarStore.get()
    }
}
