package com.shinonometn.re.ssim.application.security;

import com.shinonometn.re.ssim.service.user.entity.User;
import org.apache.shiro.authc.UsernamePasswordToken;

public class RessimUserPasswordToken extends UsernamePasswordToken{

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
