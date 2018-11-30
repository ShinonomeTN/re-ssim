package com.shinonometn.re.ssim.service.caterpillar.repository;

import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportTaskRepository extends MongoRepository<ImportTask,String>{
}
