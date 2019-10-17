package com.shinonometn.re.ssim.application.security;

import com.shinonometn.re.ssim.service.user.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public final class WebSubjectUtils {

    public static Subject currentSubject(){
        return SecurityUtils.getSubject();
    }

    public static RessimUserPasswordToken currentToken(){
        return (RessimUserPasswordToken) currentSubject().getPrincipal();
    }

    public static User currentUser(){
        return currentToken().getUser();
    }

}
