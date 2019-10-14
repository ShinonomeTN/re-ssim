package com.shinonometn.re.ssim.caterpillar.application.repository

import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CaptureTaskRepository : JpaRepository<CaptureTask, Int>
