package com.spring.develop.registrationloginspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetURL = determineTargetUrl(authentication);
        if(response.isCommitted()){
            return;
        }
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request,response,targetURL);
    }

    public String determineTargetUrl(Authentication authentication){
        String url = "";
        Collection<? extends GrantedAuthority> authority = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority grantedAuthority:authority){
            roles.add(grantedAuthority.getAuthority());
        }
        if(roles.contains("ROLE_ADMIN")){
            url = "/homepage_admin";
        }
        else if(roles.contains("ROLE_USER")){
            url = "/homepage";
        }
        return url;
    }
}
