package com.shinonometn.re.ssim.models

import com.shinonometn.re.ssim.caterpillar.kingo.pojo.Lesson
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
open class CourseEntity {

    @get:Id
    var id: String? = null
    var term: String? = null//学期
    var name: String? = null//课程名称
    var code: String? = null//课程代号
    var unit: String? = null//承担单位
    var timeSpend: Double = 0.toDouble()//学时
    var point: Double? = null//学分
    var lessons: List<Lesson>? = null//课堂
}
