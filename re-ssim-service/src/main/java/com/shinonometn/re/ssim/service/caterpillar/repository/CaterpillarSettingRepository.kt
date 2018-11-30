package com.shinonometn.re.ssim.service.caterpillar.repository

import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CaterpillarSettingRepository: MongoRepository<CaterpillarSetting,String> {

    fun findByOwnerAndName(username: String, name: String): Optional<CaterpillarSetting>

    fun findAllByOwner(username: String, pageable: Pageable): Page<CaterpillarSetting>
}