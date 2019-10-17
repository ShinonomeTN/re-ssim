package com.shinonometn.re.ssim.application.interceptor;

import com.shinonometn.re.ssim.service.statistics.StatisticsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StatisticsInterceptor extends HandlerInterceptorAdapter {

    private final StatisticsService statisticsService;

    public StatisticsInterceptor(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        statisticsService.increaseVisitorCount();
    }
}
