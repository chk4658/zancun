package com.touchspring.ailge.utils;


import com.touchspring.ailge.dto.login.UserAuthDTO;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Victor
 */
public class SecurityUtils {

    private HttpServletRequest request;

    public static UserAuthDTO getUser(@RequestHeader("token") String token) {

        UserAuthDTO user = JWTUtils.unSign(token, UserAuthDTO.class);
        return user;
    }

}
