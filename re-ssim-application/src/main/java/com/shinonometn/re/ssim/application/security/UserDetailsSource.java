package com.shinonometn.re.ssim.application.security;

public interface UserDetailsSource {
    UserDetails getUserDetailsByUsername(String username) throws UserNotFoundException;
}
