package com.spring.develop.registrationloginspring.controllers;

import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/homepage_admin")
    public String admin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User admin = userRepository.findByEmail(authentication.getName());
            model.addAttribute("admin", admin);
            return "admin";
        }
        return "login";
    }
}
