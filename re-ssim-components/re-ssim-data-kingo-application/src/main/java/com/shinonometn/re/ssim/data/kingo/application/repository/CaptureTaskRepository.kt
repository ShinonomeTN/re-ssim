package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.base.CaptureTaskStage
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CaptureTaskRepository : JpaRepository<CaptureTask, Int> {
    @Modifying
    @Query("update CaptureTask ct set ct.stage = ?2, ct.stageReport = ?3 where ct.id = ?1")
    fun updateTaskStatus(taskId: Int, captureTaskStage: CaptureTaskStage, reporting: String)
}
