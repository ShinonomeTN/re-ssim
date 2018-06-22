package com.shinonometn.re.ssim.repository;

import com.shinonometn.re.ssim.models.CaptureTask;
import com.shinonometn.re.ssim.models.CaptureTaskDTO;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CaptureTaskRepository extends CrudRepository<CaptureTask, String> {

    List<CaptureTaskDTO> findAllDTO();

    CaptureTaskDTO findOneDtoById(String id);

}
