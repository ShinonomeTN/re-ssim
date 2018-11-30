package com.shinonometn.re.ssim.application.controller.froms

import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import org.springframework.validation.Errors

class CreateCaterpillarSettingFromValidator : SaveCaterpillarSettingFromValidator() {
    override fun validate(target: Any, errors: Errors) {
        if (target is CaterpillarSetting) {
            validateBaseInfo(target, errors)

            target.id?.let { errors.reject("form_validate_failed") }
            target.owner?.let { errors.reject("from_validate_failed") }
        }
    }
}
