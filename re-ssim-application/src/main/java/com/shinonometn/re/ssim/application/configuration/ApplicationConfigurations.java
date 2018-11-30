package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningConfiguration;
import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningResultReceiver;
import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.EndpointInformation;
import com.shinonometn.re.ssim.service.statistics.ApiEndpointInfoService;
import com.shinonometn.re.ssim.service.statistics.entity.ApiEndpointInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.stream.Collectors;

@Configuration
public class ApplicationConfigurations {

    private final ApiEndpointInfoService apiEndpointInfoService;

    public ApplicationConfigurations(ApiEndpointInfoService apiEndpointInfoService) {
        this.apiEndpointInfoService = apiEndpointInfoService;
    }

    /*
     *
     * Configurations
     *
     * */

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public ApiEndpointScanningConfiguration apiEndpointScanningConfiguration() {
        return new ApiEndpointScanningConfiguration();
    }

    @Bean
    public ApiEndpointScanningResultReceiver apiEndpointScanningResultReceiver() {
        return (endpointInformation, timestamp) -> apiEndpointInfoService
                .handleEndpointScanningResult(endpointInformation.stream().map(ei -> {
                    ApiEndpointInfo apiEndpointInfo = new ApiEndpointInfo();

                    EndpointInformation.MetaInfo metaInfo = ei.getMetaInfo();
                    apiEndpointInfo.setTitle(metaInfo.getTitle());
                    apiEndpointInfo.setDescription(metaInfo.getDescription());

                    EndpointInformation.SignatureInfo signatureInfo = ei.getSignatureInfo();
                    apiEndpointInfo.setMethodSignature(signatureInfo.getMethodSignature());
                    apiEndpointInfo.setUrlSignature(signatureInfo.getRequestSignature());

                    EndpointInformation.PermissionInfo permissionInfo = ei.getPermissionInfo();
                    apiEndpointInfo.setRequiresPermissions(permissionInfo.getPermissionsRequired());

                    return apiEndpointInfo;
                }).collect(Collectors.toList()), timestamp);
    }
}
