package com.shinonometn.re.ssim.data.caterpillar

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class CaptureTask {

    @Id
    var id: String? = null
    var termCode: String? = null
    var termName: String? = null
    var createDate: Date? = null
    var stage: String? = null
    var finished: Boolean? = false

    companion object {
        const val STAGE_INIT = "initialize"
        const val STAGE_IMPORT = "import"
        const val STAGE_CAPTURE = "capture"
    }
}
