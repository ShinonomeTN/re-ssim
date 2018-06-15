package com.shinonometn.re.ssim.repository;

import com.shinonometn.re.ssim.models.CaptureTask;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

public interface CaptureTaskRepository extends CrudRepository<CaptureTask, ObjectId> {
}
