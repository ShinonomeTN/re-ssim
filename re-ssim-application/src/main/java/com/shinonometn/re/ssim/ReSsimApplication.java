package com.shinonometn.re.ssim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReSsimApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReSsimApplication.class, args);
    }
}
