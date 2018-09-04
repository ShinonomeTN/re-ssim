package com.shinonometn.re.ssim.security;

import java.util.List;

public interface UserDetails {

    String getUsername();

    Boolean isAvailable();

    List<GrantedAuthority> getGrantedAuthority();

}
