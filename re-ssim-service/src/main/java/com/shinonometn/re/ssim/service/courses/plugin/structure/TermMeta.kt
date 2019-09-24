package com.shinonometn.re.ssim.service.courses.plugin.structure

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.service.terms.TermInfoEntity
import org.springframework.beans.BeanUtils

@JsonInclude(JsonInclude.Include.NON_NULL)
class TermMeta {
    var code: String? = null
    var name: String? = null

    var courseCount: Int = 0
    var courseTypes: MutableList<String>? = null

    var minWeek: Int? = null
    var maxWeek: Int? = null

    var dataVersion: String? = null

    companion object {
        fun fromEntity(termInfoEntity: TermInfoEntity) = TermMeta().apply {
            BeanUtils.copyProperties(termInfoEntity, this, TermMeta::class.java)
        }
    }
}
