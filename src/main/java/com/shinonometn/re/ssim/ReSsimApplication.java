package com.shinonometn.re.ssim;

import com.shinonometn.re.ssim.caterpillar.SpiderMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class ReSsimApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReSsimApplication.class, args);
    }

    /*
    *
    * Configurations
    *
    * */

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
