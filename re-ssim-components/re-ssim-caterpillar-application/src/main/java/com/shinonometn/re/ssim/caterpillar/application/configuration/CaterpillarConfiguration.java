package com.shinonometn.re.ssim.caterpillar.application.configuration;

import com.shinonometn.re.ssim.service.caterpillar.SpiderMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaterpillarConfiguration {

    /**
     *
     * Create a monitor to get current tasks status conveniently
     *
     * @return SpiderMonitor
     */
    @Bean
    public SpiderMonitor spiderMonitor() {
        return new SpiderMonitor();
    }
}
