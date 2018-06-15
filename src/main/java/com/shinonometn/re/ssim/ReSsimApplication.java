package com.shinonometn.re.ssim;

import com.shinonometn.re.ssim.caterpillar.SpiderMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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


}
