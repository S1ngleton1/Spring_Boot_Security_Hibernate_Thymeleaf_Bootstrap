package com.spring.develop.registrationloginspring.service;

import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
//    boolean saveUser(User user);
    User saveUser(User user);
    Authentication getAuthentication();
    boolean activateUser(String code);
    User updateUser(User updateUser, String email);
    User getAuthenticatedUser();
}
