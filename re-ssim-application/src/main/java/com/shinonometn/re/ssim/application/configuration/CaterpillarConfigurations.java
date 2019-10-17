package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class CaterpillarConfigurations {

    @Bean
    public SpiderMonitor spiderMonitor(){
        return new SpiderMonitor();
    }

    @Bean(name = "caterpillarProperties")
    public Properties caterpillarProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./caterpillar_settings.properties"));
        return properties;
    }

}
