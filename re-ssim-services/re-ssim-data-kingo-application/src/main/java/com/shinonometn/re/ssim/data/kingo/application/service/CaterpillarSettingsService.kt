package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.data.kingo.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.data.kingo.application.repository.CaterpillarSettingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
open class CaterpillarSettingsService(private val caterpillarSettingRepository: CaterpillarSettingRepository) {
    open fun findProfile(username: String, name: String): Optional<CaterpillarSetting> = caterpillarSettingRepository.findByOwnerAndName(username, name)

    open fun save(caterpillarSetting: CaterpillarSetting): CaterpillarSetting =
            caterpillarSettingRepository.save(caterpillarSetting)

    open fun findAll(pageable: Pageable): Page<CaterpillarSetting> = caterpillarSettingRepository.findAll(pageable)

    open fun findAllByUser(username: String, pageable: Pageable): Page<CaterpillarSetting> = caterpillarSettingRepository.findAllByOwner(username, pageable)

    open fun findById(id: Int): Optional<CaterpillarSetting> = caterpillarSettingRepository.findById(id)
}
