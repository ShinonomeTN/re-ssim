package com.shinonometn.re.ssim.models

class GrantedPermission {

    enum class Type {
        PLAIN, REGEX
    }

    var expression: String? = null
    var type : Type = Type.PLAIN
}
