package com.shinonometn.re.ssim.data.kingo.application.entity

import com.shinonometn.re.ssim.data.kingo.application.commons.StringListJsonAttributeConverter
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_term_info")
class TermInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(columnDefinition = "VARCHAR(16)")
    var identity: String? = null
    @Column(columnDefinition = "VARCHAR(64)")
    var name: String? = null

    var courseCount: Int? = null

    @Convert(converter = StringListJsonAttributeConverter::class)
    @Column(columnDefinition = "TEXT")
    var courseTypes: MutableList<String>? = null

    var minWeek: Int? = null
    var maxWeek: Int? = null

    @Temporal(TemporalType.TIMESTAMP)
    var startDate: Date? = null
    @Temporal(TemporalType.TIMESTAMP)
    var endDate: Date? = null

    var dataVersion: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createDate: Date? = null
    @Temporal(TemporalType.TIMESTAMP)
    var updateDate: Date? = null

    override fun toString(): String {
        return "TermInfo(id=$id, identity=$identity, name=$name, courseCount=$courseCount, courseTypes=$courseTypes, minWeek=$minWeek, maxWeek=$maxWeek, startDate=$startDate, endDate=$endDate, dataVersion=$dataVersion, createDate=$createDate, updateDate=$updateDate)"
    }
}