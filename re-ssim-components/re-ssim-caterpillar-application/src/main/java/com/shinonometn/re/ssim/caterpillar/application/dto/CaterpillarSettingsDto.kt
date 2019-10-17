package com.shinonometn.re.ssim.caterpillar.application.dto

import com.shinonometn.re.ssim.caterpillar.application.commons.agent.CaterpillarProfileAgent
import com.shinonometn.re.ssim.caterpillar.application.entity.CaterpillarSetting

data class CaterpillarSettingsDto(private val map: MutableMap<String, Any?> = HashMap()) {
    var caterpillarSetting: CaterpillarSetting by map
    var agentProfileInfo: CaterpillarProfileAgent by map
}