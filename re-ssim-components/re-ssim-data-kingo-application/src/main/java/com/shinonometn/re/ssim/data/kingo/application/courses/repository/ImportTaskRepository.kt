package com.shinonometn.re.ssim.data.kingo.application.courses.repository

import com.shinonometn.re.ssim.data.kingo.application.courses.entity.ImportTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface ImportTaskRepository : JpaRepository<ImportTask, Int> {

    fun existsByCaptureTaskId(captureTaskId: Int?): Boolean
}
