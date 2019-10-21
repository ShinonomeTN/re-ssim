package com.shinonometn.re.ssim.data.kingo.application.courses.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.data.kingo.application.courses.commons.ImportTaskStatus
import org.springframework.data.annotation.Id
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_import_tasks")
@JsonInclude(JsonInclude.Include.NON_NULL)
class ImportTask : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    var captureTaskId: Int? = null

    var dataPath: String? = null

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(16)")
    var status: ImportTaskStatus = ImportTaskStatus.NONE
    var statusReport: String = ""

    var termCode: String? = null
    var termName: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createDate: Date? = null
    @Temporal(TemporalType.TIMESTAMP)
    var finishDate: Date? = null
}
