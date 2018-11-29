package com.shinonometn.re.ssim.application.security;

import java.util.List;

public interface UserDetails {

    String getUsername();

    Boolean isAvailable();

    List<GrantedAuthority> getGrantedAuthority();

}
