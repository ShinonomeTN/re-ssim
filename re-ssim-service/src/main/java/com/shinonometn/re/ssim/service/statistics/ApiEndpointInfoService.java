package com.shinonometn.re.ssim.service.statistics;

import com.shinonometn.re.ssim.service.statistics.entity.ApiEndpointInfo;
import com.shinonometn.re.ssim.service.statistics.repository.ApiEndpointInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiEndpointInfoService {
    private final ApiEndpointInfoRepository apiEndpointInfoRepository;

    public ApiEndpointInfoService(ApiEndpointInfoRepository apiEndpointInfoRepository) {
        this.apiEndpointInfoRepository = apiEndpointInfoRepository;
    }

    public List<ApiEndpointInfo> listAll() {
        return apiEndpointInfoRepository.findAll();
    }


}
