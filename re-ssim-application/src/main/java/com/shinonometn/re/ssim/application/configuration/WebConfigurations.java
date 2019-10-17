package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.application.interceptor.StatisticsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurations implements WebMvcConfigurer {

    private StatisticsInterceptor statisticsInterceptor;

    public WebConfigurations(StatisticsInterceptor statisticsInterceptor) {
        this.statisticsInterceptor = statisticsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(statisticsInterceptor)
                .addPathPatterns("/api/**");
    }

}
