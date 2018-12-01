package com.shinonometn.re.ssim.service.courses.plugin.structure

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class TermMeta {
    var courseCount: Int = 0
    var version: String = ""
}
