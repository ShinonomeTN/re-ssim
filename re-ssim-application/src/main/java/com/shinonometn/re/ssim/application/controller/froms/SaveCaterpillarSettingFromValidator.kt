package com.shinonometn.re.ssim.application.controller.froms

import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSetting
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace
import org.springframework.validation.Validator

open class SaveCaterpillarSettingFromValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == CaterpillarSetting::class
    }

    override fun validate(target: Any, errors: Errors) {
        if (target is CaterpillarSetting) {
            validateBaseInfo(target,errors)
        }
    }

    protected fun validateBaseInfo(terget: Any, errors: Errors) {
        rejectIfEmptyOrWhitespace(errors, "name", "caterpillar_settings_name_empty")
        rejectIfEmptyOrWhitespace(errors, "username", "caterpillar_settings_user_empty")
        rejectIfEmptyOrWhitespace(errors, "password", "caterpillar_settings_password_empty")
        rejectIfEmptyOrWhitespace(errors, "role", "caterpillar_settings_role_empty")
        rejectIfEmptyOrWhitespace(errors, "userAgent", "caterpillar_settings_userAgent_empty")
    }
}
