package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TermInfoRepository : JpaRepository<TermInfo, Int> {
    fun findByIdentity(get: String): Optional<TermInfo>
}