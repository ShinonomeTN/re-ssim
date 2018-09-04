package com.shinonometn.re.ssim.security;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiPermissionInterceptor extends HandlerInterceptorAdapter {

    private UserDetailsSource userDetailsSource;

    public ApiPermissionInterceptor(UserDetailsSource userDetailsSource) {
        this.userDetailsSource = userDetailsSource;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return true;
    }
}
