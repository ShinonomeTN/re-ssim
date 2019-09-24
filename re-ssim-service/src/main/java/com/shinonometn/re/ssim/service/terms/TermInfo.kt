package com.shinonometn.re.ssim.service.terms

import java.io.Serializable

class TermInfo : Serializable {
    var term: TermInfoEntity? = null

    var hasLocalCourseData: Boolean = false

    var hasLocalCalendarData: Boolean = false
}
