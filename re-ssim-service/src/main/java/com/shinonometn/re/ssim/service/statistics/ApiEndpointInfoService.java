package com.shinonometn.re.ssim.service.statistics;

import com.shinonometn.re.ssim.service.statistics.repository.ApiEndpointInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiEndpointInfoService {
    private final ApiEndpointInfoRepository apiEndpointInfoRepository;

    public ApiEndpointInfoService(ApiEndpointInfoRepository apiEndpointInfoRepository) {
        this.apiEndpointInfoRepository = apiEndpointInfoRepository;
    }

    
}
