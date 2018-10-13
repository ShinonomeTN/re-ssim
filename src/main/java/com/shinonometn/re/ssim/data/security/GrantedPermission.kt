package com.shinonometn.re.ssim.data.security

import java.io.Serializable

class GrantedPermission : Serializable {

    enum class Type {
        PLAIN, REGEX
    }

    var expression: String? = null
    var type: Type = Type.PLAIN
    var description: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GrantedPermission

        if (expression != other.expression) return false
        if (type != other.type) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = expression?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }


}
