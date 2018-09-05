package com.shinonometn.re.ssim.security;

import com.shinonometn.re.ssim.commons.session.HttpSessionWrapper;
import com.shinonometn.re.ssim.commons.session.SessionWrapper;
import com.shinonometn.re.ssim.services.SecurityInfoService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ApiPermissionInterceptor extends HandlerInterceptorAdapter {

    private final SecurityInfoService securityInfoService;

    public ApiPermissionInterceptor(SecurityInfoService securityInfoService) {
        this.securityInfoService = securityInfoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) return false;

        if (decide((HandlerMethod) handler, new HttpSessionWrapper(request.getSession()))) return true;

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    private boolean decide(HandlerMethod handler, SessionWrapper sessionWrapper) {

        // Has no limit, let request pass
        if (!handler.getMethod().isAnnotationPresent(AuthorityRequired.class)) return true;

        // No login
        UserDetails userDetails = sessionWrapper.getUserDetails();
        if (userDetails == null) return false;

        String name = handler.getMethodAnnotation(AuthorityRequired.class).name();

        return userDetails
                .getGrantedAuthority()
                .stream()
                .map(i -> securityInfoService.getRoleByName(i.name()))
                .filter(i -> i.getGrantedPermission() != null)
                .flatMap(i -> i.getGrantedPermission().stream())
                .anyMatch(i -> {
                    switch (i.getType()) {
                        case PLAIN:
                            return name.equals(i.getExpression());
                        case REGEX:
                            return i.getExpression() != null && name.matches(i.getExpression());
                        default:
                            return false;
                    }
                });
    }
}
