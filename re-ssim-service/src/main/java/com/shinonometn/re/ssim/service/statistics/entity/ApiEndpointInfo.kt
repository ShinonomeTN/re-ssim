package com.shinonometn.re.ssim.service.statistics.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class ApiEndpointInfo {

    var id : String? = null

    var methodSignature : String = ""
    var urlSignature: String = ""

    var requiresPermissions : MutableSet<String> = HashSet()

    var title : String = ""
    var description: String = ""

    var updateDate : Date? = null

}