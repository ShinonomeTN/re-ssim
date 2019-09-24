package com.shinonometn.re.ssim.service.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportTaskRepository extends MongoRepository<ImportTask,String>{

    boolean existsByCaptureTaskId(String captureTaskId);
}
