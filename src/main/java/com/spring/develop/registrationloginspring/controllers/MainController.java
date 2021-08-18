package com.spring.develop.registrationloginspring.controllers;

import com.spring.develop.registrationloginspring.config.LoginSuccessHandler;
import com.spring.develop.registrationloginspring.constants.ImgCarousel;
import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.PostRepository;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import com.spring.develop.registrationloginspring.service.PostService;
import com.spring.develop.registrationloginspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Autowired
    private ImgCarousel imgCarousel;

    private static final int PROFILE_PAGE_SIZE = 3;
    private static final int HOME_PAGE_SIZE = 4;
    private static final int POST_DETAIL_PAGE_SIZE = 2;


//    @GetMapping("/")
//    public String mainPage(Model model){
//        // Prevent User Going Back to Login Page If Already Logged In - start
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return mainPagePaginated(1,model);
//        }
//        String url = loginSuccessHandler.determineTargetUrl(authentication);
//        // Prevent User Going Back to Login Page If Already Logged In - end
//        return "redirect:" + url; //Redirect to user or admin page
//    }
    @GetMapping("/")
    public String mainPagePaginated(@RequestParam(name = "page",required = false) Integer pageNumber,
                                    Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if(pageNumber==null){
                pageNumber=1;
            }
            Post lastPost = postRepository.findLastPost();
            if (pageNumber <= 0) {
                return "redirect:/";
            }
            Page<Post> page = postService.findPaginated(pageNumber, HOME_PAGE_SIZE);
            List<Post> posts = page.getContent();

            model.addAttribute("totalPages", page.getTotalPages());
            if (page.getTotalPages() == 0) {
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            } else {
                model.addAttribute("currentPage", pageNumber);
            }
            if(imgCarousel.getSetOfTopics() != null){
                model.addAttribute("topicImages",imgCarousel.getSetOfTopics());
            }

            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("posts", posts);
            model.addAttribute("lastPost", lastPost);

            if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                return "redirect:/";
            }

            return "main";
        }
        String url = loginSuccessHandler.determineTargetUrl(authentication);
        // Prevent User Going Back to Login Page If Already Logged In - end
        return "redirect:" + url; //Redirect to user or admin page
    }

    @GetMapping("/{userLogin}")
    public String nonAuthenticatedUserProfile(@PathVariable(name = "userLogin") String login,
                                              @RequestParam(name = "page",required = false)Integer pageNumber,
                                              Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            User findUserByLogin = userRepository.findByLogin(login);
            if (findUserByLogin == null) {
                return "redirect:/";
            }
            if (pageNumber == null) {
                pageNumber = 1;
            }
            if (pageNumber <= 0) {
                return "redirect:/" + login;
            }
            Page<Post> page = postService.findPaginadedByUserId(pageNumber, PROFILE_PAGE_SIZE, findUserByLogin.getId());
            List<Post> posts = page.getContent();

            model.addAttribute("totalPages", page.getTotalPages());
            if (page.getTotalPages() == 0) {
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            } else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("posts", posts);
            model.addAttribute("findUserByLogin", findUserByLogin);

            if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                return "redirect:/";
            }
            return "user-profile-nonauthenticated";
        }
        String url = loginSuccessHandler.determineTargetUrl(authentication);
        // Prevent User Going Back to Login Page If Already Logged In - end
        return "redirect:" + url; //Redirect to user or admin page
    }

    @GetMapping("/thematic")
    public String thematicPage(@RequestParam(name = "topic") String topic,
                               @RequestParam(name = "page", required = false) Integer pageNumber,
                               Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (pageNumber == null) {
                pageNumber = 1;
            }
            Post lastPostByTopic = postRepository.findLastPostByTopic(topic);
            if (lastPostByTopic == null) {
                return "redirect:/";
            }
            if (pageNumber <= 0) {
                return "redirect:/";
            }
            Page<Post> page = postService.findTopicPaginated(pageNumber, HOME_PAGE_SIZE,topic);
            List<Post> posts = page.getContent();
            model.addAttribute("totalPages", page.getTotalPages());
            if (page.getTotalPages() == 0) {
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            } else {
                model.addAttribute("currentPage", pageNumber);
            }
            if(imgCarousel.getSetOfTopics() != null){
                model.addAttribute("topicImages",imgCarousel.getSetOfTopics());
            }
            Map<String,String> map = imgCarousel.getSetOfTopics();
            for(Map.Entry<String, String> item : imgCarousel.getSetOfTopics().entrySet()){
                if(item.getKey().equals(topic)){
                    model.addAttribute("topicValue",item.getKey());
                    model.addAttribute("topicImage",item.getValue());
                }
            }
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("posts", posts);
            model.addAttribute("topic", topic);
            model.addAttribute("lastPost", lastPostByTopic);

            if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                return "redirect:/";
            }
            return "thematic-page-nonauthenticated";
        }
        String url = loginSuccessHandler.determineTargetUrl(authentication);
        // Prevent User Going Back to Login Page If Already Logged In - end
        return "redirect:" + url; //Redirect to user or admin page
    }


    @GetMapping("/login")
    public String login(Model model){
        // Prevent User Going Back to Login Page If Already Logged In - start
//        String some = (String) model.asMap().get("confirm");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        User user = userRepository.findByEmail(authentication.getName());
        if(user.getActivationCode()!=null){
            model.addAttribute("error_message","Confirm your activation code!");
            return "login";
        }
        String url = loginSuccessHandler.determineTargetUrl(authentication);
        // Prevent User Going Back to Login Page If Already Logged In - end
        return "redirect:" + url; //Redirect to user or admin page
    }



    @GetMapping("/registration/activate/{code}")
    public String activateCode(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);
        if(isActivated){
            model.addAttribute("message", "User successfully activated!");
        }
        else{
            model.addAttribute("message", "Activation code is not found!");
        }
        return "login";
    }



}

//DELETE USER BY ID - start
//    @PostMapping("/")
//    public String deleteUser(@RequestParam String delete){
//        User userdelete = userRepository.findUserById(Long.parseLong(delete));
////        userRepository.deleteFromUserRole(userdelete.getId());
//        userRepository.deleteById(userdelete.getId());
//        return "/main";
//    }
//DELETE USER BY ID - END