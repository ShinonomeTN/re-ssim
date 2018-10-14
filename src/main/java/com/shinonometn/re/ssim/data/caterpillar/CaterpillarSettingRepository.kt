package com.shinonometn.re.ssim.data.caterpillar

import org.springframework.data.mongodb.repository.MongoRepository

interface CaterpillarSettingRepository: MongoRepository<CaterpillarSetting,String> {

    fun findAllByUserId(id: String?) : List<CaterpillarSetting>

    fun findByUserAndUsername(id: String?, profileName: String): CaterpillarSetting?

    fun getByUserIdAndName(s: String, name: String?): CaterpillarSetting?
}