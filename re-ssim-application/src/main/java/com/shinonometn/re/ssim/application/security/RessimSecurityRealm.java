package com.shinonometn.re.ssim.application.security;

import com.shinonometn.re.ssim.service.user.UserService;
import com.shinonometn.re.ssim.service.user.entity.User;
import com.shinonometn.re.ssim.service.user.entity.UserPermissionInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RessimSecurityRealm extends AuthorizingRealm {

    private final UserService userService;

    public RessimSecurityRealm(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "ressimSecurityRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token.getClass().isAssignableFrom(RessimUserPasswordToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        RessimUserPasswordToken ressimToken = (RessimUserPasswordToken) token;

        User user = userService.findByUsername(ressimToken.getUsername()).orElseThrow(UnknownAccountException::new);
        if (!Objects.equals(user.getPassword(), String.copyValueOf(ressimToken.getPassword())))
            throw new IncorrectCredentialsException();
        if (!user.getEnable()) throw new LockedAccountException();

        ressimToken.setUser(user);

        return new SimpleAuthenticationInfo(ressimToken, ressimToken.getCredentials(), getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return principals
                .byType(RessimUserPasswordToken.class)
                .stream()
                .findFirst()
                .map(token -> {
                    UserPermissionInfo permissionInfo = userService.getUserPermissions(token.getUsername());

                    SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                    simpleAuthorizationInfo.addRoles(permissionInfo.getRoles());
                    simpleAuthorizationInfo.addStringPermissions(permissionInfo.getPermissions());

                    return simpleAuthorizationInfo;
                }).orElse(null);
    }
}
