package com.shinonometn.re.ssim.application.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileServiceConfiguration {

    @Value("${app.dataDir:./}")
    private String dataDir = "./";

    @Bean(name = "dataDirectory")
    public File dataDirectory() {
        return new File(dataDir);
    }
}
