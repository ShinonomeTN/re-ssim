package com.shinonometn.re.ssim.service.statistics.repository;

import com.shinonometn.re.ssim.service.statistics.entity.ApiEndpointInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApiEndpointInfoRepository extends MongoRepository<ApiEndpointInfo, String> {
    Optional<ApiEndpointInfo> findByMethodSignature(String methodSignature);
}
