package com.shinonometn.re.ssim.service.caterpillar.repository

import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask
import org.springframework.data.mongodb.repository.MongoRepository

interface CaptureTaskRepository : MongoRepository<CaptureTask, String> {

}
