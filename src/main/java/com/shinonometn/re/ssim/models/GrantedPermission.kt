package com.shinonometn.re.ssim.models

import java.io.Serializable

class GrantedPermission : Serializable {

    enum class Type {
        PLAIN, REGEX
    }

    var expression: String? = null
    var type: Type = Type.PLAIN
}
