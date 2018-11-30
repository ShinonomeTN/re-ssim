package com.shinonometn.re.ssim.service.user.entity

class UserPermissionInfo {
    var user : User? = null
    var roles : Set<String> = HashSet()
    var permissions : Set<String> = HashSet()
}
