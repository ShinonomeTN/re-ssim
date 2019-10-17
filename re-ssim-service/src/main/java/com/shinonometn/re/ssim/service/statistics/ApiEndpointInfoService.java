package com.shinonometn.re.ssim.service.statistics;

import com.shinonometn.re.ssim.service.statistics.entity.ApiEndpointInfo;
import com.shinonometn.re.ssim.service.statistics.repository.ApiEndpointInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public void handleEndpointScanningResult(List<ApiEndpointInfo> apiEndpointInfo, Date timestamp) {
        apiEndpointInfo.forEach(e -> {
            ApiEndpointInfo forSave = apiEndpointInfoRepository.findByMethodSignature(e.getMethodSignature()).orElse(new ApiEndpointInfo());

            BeanUtils.copyProperties(e, forSave, "id", "updateDate");
            forSave.setUpdateDate(timestamp);

            apiEndpointInfoRepository.save(forSave);
        });
    }
}
