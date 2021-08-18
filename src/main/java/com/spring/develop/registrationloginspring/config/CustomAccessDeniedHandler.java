package com.spring.develop.registrationloginspring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    public static final Logger LOG
            = Logger.getLogger(CustomAccessDeniedHandler.class.toString());
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();
        String url = loginSuccessHandler.determineTargetUrl(auth);
        if (auth != null) {
            LOG.warning("User: " + auth.getName()
                    + " attempted to access the protected URL: "
                    + httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + url);
    }
}
