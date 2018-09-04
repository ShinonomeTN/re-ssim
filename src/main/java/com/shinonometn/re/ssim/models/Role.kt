package com.shinonometn.re.ssim.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Role {

    @Id
    var id : String? = null

    var name: String? = null

    var enabled: Boolean = true

    var grantedPermission: MutableList<GrantedPermission>? = null
}
