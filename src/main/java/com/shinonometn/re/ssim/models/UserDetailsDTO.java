package com.shinonometn.re.ssim.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsDTO implements UserDetails {

    private User user;
    private List<AttributeGrantedAuthority> attributeGrantedAuthorities;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(User user) {
        this.user = user;
    }

    public UserDetailsDTO(User user, List<AttributeGrantedAuthority> attributeGrantedAuthorities) {
        this.user = user;
        this.attributeGrantedAuthorities = attributeGrantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<AttributeGrantedAuthority> getAttributeGrantedAuthorities() {
        return attributeGrantedAuthorities;
    }

    public void setAttributeGrantedAuthorities(List<AttributeGrantedAuthority> attributeGrantedAuthorities) {
        this.attributeGrantedAuthorities = attributeGrantedAuthorities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
