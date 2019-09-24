package com.shinonometn.re.ssim.caterpillar.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;

@SpringBootApplication
public class ReSsimCaterpillarApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReSsimCaterpillarApplication.class, args);
    }

    @Value("${application.data.path}")
    private String rootFolderPath;

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public File rootFolder() {
        File file = new File(rootFolderPath);
        if (!file.exists() && !file.mkdirs())
            throw new RuntimeException("Could not create folder " + file.getAbsolutePath() + ".");

        return file;
    }
}
