package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.controller.froms.SaveCaterpillarSettingFromValidator
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSetting
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSettingRepository
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caterpillar/profiles")
class CaterpillarProfileController(private val caterpillarSettingRepository: CaterpillarSettingRepository) {

    @PostMapping
    @RequiresPermissions("profile:caterpillar:create")
    fun createProfile(@RequestBody caterpillarSetting: CaterpillarSetting) {

        caterpillarSettingRepository.getByUserIdAndName("userId",caterpillarSetting.name)?.let{
            throw com.shinonometn.re.ssim.commons.BusinessException("caterpillar_setting_name_duplicated")
        }

        caterpillarSettingRepository.save(caterpillarSetting)
    }

    @PutMapping
    @RequiresPermissions("profile:caterpillar:write")
    fun saveProfile(@Validated(SaveCaterpillarSettingFromValidator::class) @RequestBody caterpillarSetting: CaterpillarSetting) {
        caterpillarSettingRepository.save(caterpillarSetting)
    }

}
