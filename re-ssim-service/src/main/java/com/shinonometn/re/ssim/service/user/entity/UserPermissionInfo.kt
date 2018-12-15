package com.shinonometn.re.ssim.service.user.entity

import java.io.Serializable

class UserPermissionInfo: Serializable {
    var user : User? = null
    var roles : Set<String> = HashSet()
    var permissions : Set<String> = HashSet()
}
