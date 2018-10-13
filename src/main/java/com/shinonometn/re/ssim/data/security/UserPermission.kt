package com.shinonometn.re.ssim.data.security

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class UserPermission {

    @Id
    var id: String? = null
    var userId: String? = null
    var roles: Set<String> = HashSet()

}
