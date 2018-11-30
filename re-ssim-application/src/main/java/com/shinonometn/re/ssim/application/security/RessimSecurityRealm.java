package com.shinonometn.re.ssim.application.security;

import com.shinonometn.re.ssim.service.user.PermissionService;
import com.shinonometn.re.ssim.service.user.RoleService;
import com.shinonometn.re.ssim.service.user.UserService;
import com.shinonometn.re.ssim.service.user.entity.Permission;
import com.shinonometn.re.ssim.service.user.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RessimSecurityRealm extends AuthorizingRealm {

    private final UserService userService;
    private final PermissionService permissionService;
    private final RoleService roleService;

    public RessimSecurityRealm(UserService userService, PermissionService permissionService, RoleService roleService) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.roleService = roleService;
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
                    Permission permission = permissionService.findByUser(token.getUsername()).orElse(null);
                    if (permission == null) return null;

                    Set<String> permissionList = permission
                            .getRoles()
                            .stream()
                            .map(roleService::findByName)
                            .filter(Optional::isPresent)
                            .flatMap(e -> e.get().getPermissions().stream())
                            .collect(Collectors.toSet());

                    permissionList.addAll(permission.getExtraPermissions());

                    SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                    simpleAuthorizationInfo.addRoles(permission.getRoles());
                    simpleAuthorizationInfo.addStringPermissions(permissionList);

                    return simpleAuthorizationInfo;
                }).orElse(null);
    }
}
