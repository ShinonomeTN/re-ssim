package com.shinonometn.re.ssim.data.kingo.application.api

import com.shinonometn.re.ssim.data.kingo.application.service.TermDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/term")
class TermInfoAPI(private val termDataService: TermDataService) {

//    /**
//     *
//     * List all terms
//     *
//     */
//    @GetMapping
//    fun list(): Collection<TermMeta>? {
//        TODO("Term storage and term detailed info")
//    }
//
//    /**
//     *
//     * Get school calendar
//     *
//     * */
//    @GetMapping("/{name}/calendar")
//    fun calendar(@PathVariable("name") term: String) = when (term) {
//        "current" -> TODO("School calendar")
//        else -> TODO("School calendar")
//    }

    /**
     *
     * List termName courses
     *
     */
    @GetMapping("/{name}/course")
    fun listTermCourse(@PathVariable("name") termName: String) =
            termDataService.listAllCoursesOfTerm(termName, "{dataVersion}")

    /**
     *
     * List all teacher presented in termName
     *
     */
    @GetMapping("/{name}/teacher")
    fun listTermTeachers(@PathVariable("name") termName: String) =
            termDataService.listTeachersOfTerm(termName, "{dataVersion}")

    /**
     *
     * List all termName class
     *
     */
    @GetMapping("/{name}/class")
    fun listTermClasses(@PathVariable("name") termName: String) =
            termDataService.listClassesOfTerm(termName, "{dataVersion}")


    /**
     *
     * List all classrooms that used in termName
     *
     */
    @GetMapping("/{name}/classroom")
    fun listTermClassrooms(@PathVariable("name") termName: String) =
            termDataService.listClassroomsOfTerm(termName, "{dataVersion}")
}
