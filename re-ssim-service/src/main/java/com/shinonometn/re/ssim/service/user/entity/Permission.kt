package com.shinonometn.re.ssim.service.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
class Permission : Serializable {
    
    @Id
    var id : String? = null
    
    var user : String? = null
    
    var roles : MutableSet<String> = HashSet()
    
    var extraPermissions : MutableSet<String> = HashSet() 

    var updateDate: Date? = null
}
