package com.shinonometn.re.ssim.caterpillar.application.repository

import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CaptureTaskRepository : MongoRepository<CaptureTask, String>
