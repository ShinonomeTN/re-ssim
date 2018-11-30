package com.shinonometn.re.ssim.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = "com.shinonometn.re.ssim")
public class ReSsimApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReSsimApplication.class, args);
    }
}
