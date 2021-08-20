package com.spring.develop.registrationloginspring.controllers;

import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (status != null) {
                Integer statusCode = Integer.valueOf(status.toString());

                if(statusCode == HttpStatus.NOT_FOUND.value()) {
                    model.addAttribute("title","Page cannot be found");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","Sorry, we're unable to bring you the page you're looking for !!!");
                    model.addAttribute("errorMessage2","Please try to double check the URL.");
                    return "error/error-page-nonauthenticated";
                }
                else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    model.addAttribute("title","Internal server error");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","An internal server error occurred !!!");
                    model.addAttribute("errorMessage2","Please try to repeat the request.");
                    return "error/error-page-nonauthenticated";
                }
                else if(statusCode == HttpStatus.BAD_REQUEST.value()) {
                    model.addAttribute("title","Bad request");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","Sorry, we're unable to bring you the page you're looking for !!!");
                    model.addAttribute("errorMessage2","Please try to check request parameters.");
                    return "error/error-page-nonauthenticated";
                }

            }
        } else{
            User user = userRepository.findByEmail(authentication.getName());
            model.addAttribute("user",user);
            if (status != null) {
                Integer statusCode = Integer.valueOf(status.toString());

                if(statusCode == HttpStatus.NOT_FOUND.value()) {
                    model.addAttribute("title","Page cannot be found");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","Sorry, we're unable to bring you the page you're looking for !!!");
                    model.addAttribute("errorMessage2","Please try to double check the URL.");
                    return "error/error-page";
                }
                else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    model.addAttribute("title","Internal server error");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","An internal server error occurred !!!");
                    model.addAttribute("errorMessage2","Please try to repeat the request.");
                    return "error/error-page";
                }
                else if(statusCode == HttpStatus.BAD_REQUEST.value()) {
                    model.addAttribute("title","Bad request");
                    model.addAttribute("statusCode",statusCode);
                    model.addAttribute("errorMessage","Sorry, we're unable to bring you the page you're looking for !!!");
                    model.addAttribute("errorMessage2","Please try to check request parameters.");
                    return "error/error-page";
                }

            }
        }

        return "error";
    }
}
