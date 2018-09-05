package com.shinonometn.re.ssim;

import com.shinonometn.re.ssim.security.ApiPermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurations implements WebMvcConfigurer{

    private ApiPermissionInterceptor apiPermissionInterceptor;

    public WebConfigurations(ApiPermissionInterceptor apiPermissionInterceptor) {
        this.apiPermissionInterceptor = apiPermissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiPermissionInterceptor)
                .excludePathPatterns("/static/**");
    }

}
