package com.shinonometn.re.ssim.commons.session;

import com.shinonometn.re.ssim.security.UserDetails;

import javax.servlet.http.HttpSession;

public class HttpSessionWrapper implements SessionWrapper{

    private HttpSession session;

    public HttpSessionWrapper(HttpSession session) {
        this.session = session;
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }

    @Override
    public UserDetails getUserDetails() {
        return (UserDetails) session.getAttribute("session.login");
    }

    @Override
    public void setUserDetails(UserDetails userDetails) {
        session.setAttribute("session.login",userDetails);
    }
}
