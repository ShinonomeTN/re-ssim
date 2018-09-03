package com.shinonometn.re.ssim.models

import org.springframework.security.core.GrantedAuthority

class AttributeGrantedAuthority : GrantedAuthority {

    var permission: String? = null

    constructor()

    constructor(expression: String){
        permission = expression
    }

    override fun getAuthority(): String? {
        return permission
    }
}
