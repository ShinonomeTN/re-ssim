package com.shinonometn.re.ssim.application.interceptor;

import com.shinonometn.re.ssim.services.ManagementService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StatisticsInterceptor extends HandlerInterceptorAdapter {

    private final ManagementService managementService;

    public StatisticsInterceptor(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        managementService.increaseVisitCount();
    }
}
