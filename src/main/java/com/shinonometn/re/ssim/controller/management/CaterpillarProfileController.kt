package com.shinonometn.re.ssim.controller.management

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.controller.froms.CreateCaterpillarSettingFromValidator
import com.shinonometn.re.ssim.controller.froms.SaveCaterpillarSettingFromValidator
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSetting
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSettingRepository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caterpillar/profiles")
class CaterpillarProfileController(private val caterpillarSettingRepository: CaterpillarSettingRepository) {

    @PostMapping
    fun createProfile(@Validated(CreateCaterpillarSettingFromValidator::class) @RequestBody caterpillarSetting: CaterpillarSetting) {
        TODO("Shiro part")
        caterpillarSettingRepository.getByUserIdAndName("userId",caterpillarSetting.name)?.let{
            throw BusinessException("caterpillar_setting_name_duplicated")
        }

        caterpillarSettingRepository.save(caterpillarSetting)
    }

    @PutMapping
    fun saveProfile(@Validated(SaveCaterpillarSettingFromValidator::class) @RequestBody caterpillarSetting: CaterpillarSetting) {
        caterpillarSettingRepository.save(caterpillarSetting)
    }

}
