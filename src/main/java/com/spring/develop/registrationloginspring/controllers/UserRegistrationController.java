package com.spring.develop.registrationloginspring.controllers;

import com.spring.develop.registrationloginspring.config.LoginSuccessHandler;
import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.service.UserService;
import com.spring.develop.registrationloginspring.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private Environment environment;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto(){
//        return new UserRegistrationDto();
//    }

    @GetMapping()
    public String showRegistrationForm(@ModelAttribute("user") User user){
        // Prevent User Going Back to Registration Page If Already Logged In - start
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "registration";
        }
        String url = loginSuccessHandler.determineTargetUrl(authentication);
        // Prevent User Going Back to Registration Page If Already Logged In - end
        return "redirect:" + url;
    }
    @PostMapping
    public String registerUserAccount(@ModelAttribute("user")  User user, BindingResult bindingResult,
                                      Model model, RedirectAttributes redirectAttributes,
                                      @RequestParam(name = "fileImage") MultipartFile multipartFile) throws IOException {
        String fileMame = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setImage(fileMame);
//        List<String> errorList = new ArrayList<>();
//        if(user.getImage().isEmpty() || user.getImage().equals("")){
//            errorList.add(environment.getRequiredProperty("Required"));
//        }
//        if(!(user.getImage().contains(".png") || user.getImage().contains(".jpg"))){
//            errorList.add(environment.getRequiredProperty("Upload.invalid.file.type"));
//        }
//        if(!errorList.isEmpty()){
//            model.addAttribute("image_errors",errorList);
//        }


//        if(bindingResult.hasErrors()){
//            return "registration";
//        }

        userValidator.validate(user,bindingResult);
        System.out.println("qweqeqweqweqeq" + bindingResult.getFieldError());
        if(bindingResult.hasErrors()){
            return "/registration";
        }
        User savedUser = userService.saveUser(user);
        String uploadDir = "./user-img/" + savedUser.getId();
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(fileMame);
            System.out.println("MYPATH = " + filePath.toFile().getAbsolutePath());
            Files.copy(inputStream, filePath,StandardCopyOption.REPLACE_EXISTING);

        }
        catch (Exception exception){
            exception.printStackTrace();
        }
//        if(!userService.saveUser(user)){
//            model.addAttribute("message","User already exists!");
//            return "registration";
//        }
//        String userFromService = userService.saveUser(user);
//
//        if(userFromService.contains("@")){
//            model.addAttribute("message","User with the same Email already exists ");
//            return "registration";
//        }
//        if(!userFromService.isEmpty()){
//            model.addAttribute("message","User with the same Login already exists ");
//            return "registration";
//        }
        redirectAttributes.addFlashAttribute("confirm", "To confirm your registration, go to the link that was sent to your email!");
        return "redirect:/login";
    }
//    @GetMapping("/activate/{code}")
//    public String activateCode(Model model, @PathVariable String code){
//        boolean isActivated = userService.activateUser(code);
//        if(isActivated){
//            model.addAttribute("message", "User successfully activated!");
//        }
//        else{
//            model.addAttribute("message", "Activation code is not found!");
//        }
//        return "login";
//    }

//    @PostMapping
//    public String registerUserAccount(@ModelAttribute("user") User user, BindingResult bindingResult, Model model){
//        userValidator.validate(user,bindingResult);
//        if(bindingResult.hasErrors()){
//            return "/registration";
//        }
//        userService.save(user);
////        securityService.autoLogin(user.getEmail(),user.getConfirmPassword());
////        userService.save(userDto);
//        return "redirect:/homepage?success";
//    }
}
