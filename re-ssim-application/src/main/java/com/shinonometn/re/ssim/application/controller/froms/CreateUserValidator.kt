package com.shinonometn.re.ssim.application.controller.froms

import com.shinonometn.re.ssim.data.security.User
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils.rejectIfEmpty
import org.springframework.validation.Validator

class CreateUserValidator : Validator {

    override fun supports(clazz: Class<*>): Boolean = clazz == User::class.java

    override fun validate(target: Any, errors: Errors) {
        if(target is User) {
            rejectIfEmpty(errors,"username","user_name_empty")
            rejectIfEmpty(errors,"password","password_empty")
        }
    }
}
