package com.shinonometn.re.ssim.service.caterpillar.repository

import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CaterpillarSettingRepository: MongoRepository<CaterpillarSetting,String> {

    fun findAllByUserId(id: String?) : List<CaterpillarSetting>

    fun findByUserAndUsername(id: String?, profileName: String): CaterpillarSetting?


    fun findByOwnerAndName(username: String, name: String): Optional<CaterpillarSetting>
}