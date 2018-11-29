package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.commons.web.StatisticsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurations implements WebMvcConfigurer {

    //    private ApiPermissionInterceptor apiPermissionInterceptor;
    private StatisticsInterceptor statisticsInterceptor;

    public WebConfigurations(StatisticsInterceptor statisticsInterceptor) {
//        this.apiPermissionInterceptor = apiPermissionInterceptor;
        this.statisticsInterceptor = statisticsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(apiPermissionInterceptor)
//                .excludePathPatterns("/static/**");
//
        registry.addInterceptor(statisticsInterceptor)
                .addPathPatterns("/api/**");
    }

}
