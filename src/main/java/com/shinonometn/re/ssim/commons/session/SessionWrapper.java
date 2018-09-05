package com.shinonometn.re.ssim.commons.session;

import com.shinonometn.re.ssim.security.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public interface SessionWrapper {

    boolean isNew();

    UserDetails getUserDetails();

    void setUserDetails(UserDetails userDetails);
}
