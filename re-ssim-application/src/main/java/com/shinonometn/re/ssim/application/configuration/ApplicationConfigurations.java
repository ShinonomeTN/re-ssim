package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.service.commons.InMemoryStore;
import com.shinonometn.re.ssim.service.commons.InMemoryStoreManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
public class ApplicationConfigurations {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(32);
        return taskExecutor;
    }

    @Bean
    public InMemoryStoreManager inMemoryStoreManager(List<InMemoryStore> storeList) {
        return new InMemoryStoreManager(storeList);
    }
}
