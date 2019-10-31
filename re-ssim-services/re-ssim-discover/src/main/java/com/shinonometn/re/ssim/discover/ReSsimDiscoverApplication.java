package com.shinonometn.re.ssim.discover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ReSsimDiscoverApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReSsimDiscoverApplication.class, args);
    }
}
