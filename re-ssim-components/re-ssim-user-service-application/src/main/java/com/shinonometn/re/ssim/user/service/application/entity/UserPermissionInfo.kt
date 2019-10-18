package com.shinonometn.re.ssim.user.service.application.entity

import com.shinonometn.re.ssim.user.service.application.entity.User
import java.io.Serializable

class UserPermissionInfo: Serializable {
    var user : User? = null
    var roles : Set<String> = HashSet()
    var permissions : Set<String> = HashSet()
}
