package com.shinonometn.re.ssim.data.manager.application.repository;

import com.shinonometn.re.ssim.service.data.ImportTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportTaskRepository extends MongoRepository<ImportTask,String>{

    boolean existsByCaptureTaskId(String captureTaskId);
}
