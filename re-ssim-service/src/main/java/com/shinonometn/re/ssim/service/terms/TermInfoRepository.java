package com.shinonometn.re.ssim.service.terms;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TermInfoRepository extends MongoRepository<TermInfoEntity, String> {

    Optional<TermInfoEntity> findByName(String name);
    Optional<TermInfoEntity> findByCode(String key);
}
