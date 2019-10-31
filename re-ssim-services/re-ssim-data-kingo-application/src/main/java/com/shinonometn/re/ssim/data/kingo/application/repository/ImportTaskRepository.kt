package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.entity.ImportTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ImportTaskRepository : JpaRepository<ImportTask, Int> {

    @Query("select it from ImportTask it where it.captureTaskId = ?1")
    fun findByCaptureTaskId(id: Int): Optional<ImportTask>

    @Modifying
    fun deleteByCaptureTaskId(id: Int)
}
