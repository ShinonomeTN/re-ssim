package com.shinonometn.re.ssim.security;

public interface UserDetailsSource {
    UserDetails getUserDetailsByUsername(String username);
}
