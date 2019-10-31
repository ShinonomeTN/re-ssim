package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.entity.CaterpillarSetting
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CaterpillarSettingRepository : JpaRepository<CaterpillarSetting, Int> {

    fun findByOwnerAndName(username: String, name: String): Optional<CaterpillarSetting>

    fun findAllByOwner(username: String, pageable: Pageable): Page<CaterpillarSetting>
}