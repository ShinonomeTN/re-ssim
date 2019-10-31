package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TermInfoRepository : JpaRepository<TermInfo, Int> {
    fun findByIdentity(get: String): Optional<TermInfo>

    @Query("select ti from TermInfo ti where ti.endDate is not null and ti.endDate >= ?1")
    fun findByDateAfter(current: Date): Collection<TermInfo>
}