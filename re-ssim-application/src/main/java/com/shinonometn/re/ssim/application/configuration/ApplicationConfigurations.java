package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningConfiguration;
import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningResultReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ApplicationConfigurations {

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
        return (endpointInformation, timestamp) -> {

        };
    }
}
