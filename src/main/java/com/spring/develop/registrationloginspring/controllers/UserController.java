package com.spring.develop.registrationloginspring.controllers;

import com.spring.develop.registrationloginspring.constants.ImgCarousel;
import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.PostRepository;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import com.spring.develop.registrationloginspring.service.PostService;
import com.spring.develop.registrationloginspring.service.UserService;
import com.spring.develop.registrationloginspring.validator.PostValidator;
import com.spring.develop.registrationloginspring.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private static final int PROFILE_PAGE_SIZE = 3;
    private static final int HOME_PAGE_SIZE = 4;
    private static final int POST_DETAIL_PAGE_SIZE = 2;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private PostValidator postValidator;

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ImgCarousel imgCarousel;

    @GetMapping("/homepage")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user.getActivationCode() != null) {
                model.addAttribute("error_message", "Confirm your activation code!");
                return "redirect:/login";
            } else {
                return homePaginated(1,model);
            }
        }
        return "redirect:/login";
    }
    @GetMapping("/homepage/{pageNumber}")
    public String homePaginated(@PathVariable(name = "pageNumber") int pageNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user.getActivationCode() != null) {
                model.addAttribute("error_message", "Confirm your activation code!");
                return "redirect:/login";
        } else {
                Post lastPost = postRepository.findLastPost();
            if (pageNumber <= 0) {
                return "redirect:/homepage";
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
            model.addAttribute("user", user);
            model.addAttribute("lastPost", lastPost);

            if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                return "redirect:/homepage";
            }
            return "homepage";
        }
    }
        return "redirect:/login";
    }

    @GetMapping("/homepage/user/{userLogin}")
    public String showUser(@PathVariable(name = "userLogin") String login,
                           Model model){
        return showUserPaginated(login,1,model);
    }
    @GetMapping("/homepage/user/{userLogin}/{pageNumber}")
    public String showUserPaginated(@PathVariable(name = "userLogin") String login,
                                    @PathVariable(name = "pageNumber")int pageNumber,
                                    Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user.getActivationCode() != null) {
                model.addAttribute("error_message", "Confirm your activation code!");
                return "redirect:/login";
            } else {
                if(login.equals(user.getLogin())){
                    return "redirect:/homepage/profile";
                }
                User findUserByLogin = userRepository.findByLogin(login);
                if(findUserByLogin == null){
                    return "redirect:/homepage";
                }
                if (pageNumber <= 0) {
                    return "redirect:/homepage/" + login;
                }
                Page<Post> page = postService.findPaginadedByUserId(pageNumber, PROFILE_PAGE_SIZE,findUserByLogin.getId());
                List<Post> posts = page.getContent();

                model.addAttribute("totalPages", page.getTotalPages());
                if(page.getTotalPages() == 0){
                    pageNumber = 1;
                    model.addAttribute("currentPage", pageNumber);
                }
                else {
                    model.addAttribute("currentPage", pageNumber);
                }
                model.addAttribute("totalItems", page.getTotalElements());
                model.addAttribute("posts", posts);
                model.addAttribute("user", user);
                model.addAttribute("findUserByLogin", findUserByLogin);

                if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                    return "redirect:/homepage/profile";
                }
                return "user-profile";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/homepage/thematic/{topic}")
    public String thematicPage(@PathVariable(name = "topic") String topic, Model model){
        Authentication authentication = userService.getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user.getActivationCode() != null) {
                model.addAttribute("error_message", "Confirm your activation code!");
                return "redirect:/login";
            } else {
                Post lastPostByTopic = postRepository.findLastPostByTopic(topic);
                if (lastPostByTopic == null) {
                    return "redirect:/homepage";
                }

                return thematicPagePaginated(topic,1,model);
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/homepage/thematic/{topic}/{pageNumber}")
    public String thematicPagePaginated(@PathVariable(name = "topic") String topic,
                                        @PathVariable(name = "pageNumber") int pageNumber,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user.getActivationCode() != null) {
                model.addAttribute("error_message", "Confirm your activation code!");
                return "redirect:/login";
            } else {
                Post lastPostByTopic = postRepository.findLastPostByTopic(topic);
                if (lastPostByTopic == null) {
                    return "redirect:/homepage";
                }
                if (pageNumber <= 0) {
                    return "redirect:/homepage";
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
                model.addAttribute("user", user);
                model.addAttribute("lastPost", lastPostByTopic);

                if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                    return "redirect:/homepage";
                }
                return "thematic-page";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/homepage/profile")
    public String showUserProfile(Model model) {
        return userProfilePaginated(1, model);
    }

    @GetMapping("/homepage/profile/{pageNumber}")
    public String userProfilePaginated(@PathVariable(name = "pageNumber") int pageNumber, Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            if (pageNumber <= 0) {
                return "redirect:/homepage/profile";
            }
            Page<Post> page = postService.findPaginadedByUserId(pageNumber, PROFILE_PAGE_SIZE, user.getId());
            List<Post> posts = page.getContent();
            model.addAttribute("totalPages", page.getTotalPages());
            if(page.getTotalPages() == 0){
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            }
            else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("posts", posts);
            model.addAttribute("user", user);
            if (page.getTotalPages() != 0 && pageNumber > page.getTotalPages()) {
                return "redirect:/homepage/profile";
            }
            return "my-profile";
        }
    }

    @GetMapping("/homepage/profile/{postID}/{page}/{pageNumber}")
    public String showPostDetailProfile(@PathVariable(name = "postID") Long postId,
                                        @PathVariable(name = "page", required = false) int page,
                                        @PathVariable(name = "pageNumber", required = false) int pageNumber,
                                        Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return "redirect:/homepage";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            if (page <= 0) {
                return "redirect:/homepage/profile";
            }
            Page<Post> pagePost = postService.findPaginatedExceptIdCategory(pageNumber, POST_DETAIL_PAGE_SIZE, post.getId(),post.getCategory());
            List<Post> posts = pagePost.getContent();
            model.addAttribute("totalPages", pagePost.getTotalPages());
            if(pagePost.getTotalPages() == 0){
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            }
            else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", pagePost.getTotalElements());
            model.addAttribute("posts", posts);

            if (pagePost.getTotalPages() != 0 && pageNumber > pagePost.getTotalPages()) {
                return "redirect:/homepage/profile";
            }
            model.addAttribute("user", user);
            model.addAttribute("postDetail", post);
            model.addAttribute("page", page);
            return "postDetail";
        }
    }
    @GetMapping("/homepage/user/{userLogin}/{postID}/{page}/{pageNumber}")
    public String showPostDetailUser(@PathVariable(name = "userLogin") String login,
                                     @PathVariable(name = "postID") Long postId,
                                     @PathVariable(name = "page", required = false) int page,
                                     @PathVariable(name = "pageNumber", required = false) int pageNumber,
                                     Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return "redirect:/homepage";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            if (page <= 0) {
                return "redirect:/homepage/user/" + login;
            }
            Page<Post> pagePost = postService.findPaginatedExceptIdCategory(pageNumber, POST_DETAIL_PAGE_SIZE, post.getId(),post.getCategory());
            List<Post> posts = pagePost.getContent();

            model.addAttribute("totalPages", pagePost.getTotalPages());
            if(pagePost.getTotalPages() == 0){
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            }
            else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", pagePost.getTotalElements());
            model.addAttribute("posts", posts);

            if (pagePost.getTotalPages() != 0 && pageNumber > pagePost.getTotalPages()) {
                return "redirect:/homepage/user/" + login;
            }
            model.addAttribute("user", user);
            model.addAttribute("login", login);
            model.addAttribute("postDetail", post);
            model.addAttribute("page", page);
            return "postDetail";
        }
    }
    @GetMapping("/homepage/{postID}/{page}/{pageNumber}")
    public String showPostDetailHome(@PathVariable(name = "postID") Long postId,
                                     @PathVariable(name = "page", required = false) int page,
                                     @PathVariable(name = "pageNumber", required = false) int pageNumber,
                                     Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return "redirect:/homepage";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            if (page <= 0) {
                return "redirect:/homepage";
            }
            Page<Post> pagePost = postService.findPaginatedExceptIdCategory(pageNumber, POST_DETAIL_PAGE_SIZE, post.getId(),post.getCategory());
            List<Post> posts = pagePost.getContent();

            model.addAttribute("totalPages", pagePost.getTotalPages());
            if(pagePost.getTotalPages() == 0){
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            }
            else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", pagePost.getTotalElements());
            model.addAttribute("posts", posts);
            if (pagePost.getTotalPages() != 0 && pageNumber > pagePost.getTotalPages()) {
                return "redirect:/homepage";
            }
            model.addAttribute("user", user);
            model.addAttribute("postDetail", post);
            model.addAttribute("page", page);
            return "postDetail";
        }
    }

    @GetMapping("/homepage/thematic/{topic}/{postID}/{page}/{pageNumber}")
    public String showPostDetailHomeThematic(@PathVariable(name = "topic") String topic,
                                             @PathVariable(name = "postID") Long postId,
                                             @PathVariable(name = "page", required = false) int page,
                                             @PathVariable(name = "pageNumber", required = false) int pageNumber,
                                             Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return "redirect:/homepage";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            if (page <= 0) {
                return "redirect:/homepage/thematic/" + topic;
            }
            Page<Post> pagePost = postService.findPaginatedExceptIdCategory(pageNumber, POST_DETAIL_PAGE_SIZE, post.getId(),post.getCategory());
            List<Post> posts = pagePost.getContent();
            model.addAttribute("totalPages", pagePost.getTotalPages());
            if(pagePost.getTotalPages() == 0){
                pageNumber = 1;
                model.addAttribute("currentPage", pageNumber);
            }
            else {
                model.addAttribute("currentPage", pageNumber);
            }
            model.addAttribute("totalItems", pagePost.getTotalElements());
            model.addAttribute("posts", posts);
            if (pagePost.getTotalPages() != 0 && pageNumber > pagePost.getTotalPages()) {
                return "redirect:/homepage/thematic/" + topic;
            }
            model.addAttribute("user", user);
            model.addAttribute("topic", topic);
            model.addAttribute("postDetail", post);
            model.addAttribute("page", page);
            return "postDetail";
        }
    }

    @GetMapping("/homepage/{postID}/{page}/edit")
    public String editPost(@PathVariable(name = "postID") Long postId,
                           @PathVariable(name = "page") int page,
                           Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return "redirect:/homepage";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("editPost", post);
            model.addAttribute("page", page);
            return "editPost";
        }
    }




    @PostMapping("/homepage/{postID}/{page}/edit")
    public String updatePost(@PathVariable(name = "postID") Long postId,
                             @PathVariable(name = "page") int page,
                             @RequestParam(value = "postDate", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate postDate,
                             @ModelAttribute("editPost") Post editPost,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        editPost.setPostDate(postDate);
        postValidator.validate(editPost, bindingResult);
        if (bindingResult.hasErrors()) {
            editPost.setId(postId);
            Authentication authentication = userService.getAuthentication();
            model.addAttribute("user", userRepository.findByEmail(authentication.getName()));
            return "editPost";
        }
        Post updatedPost = postService.updatePost(editPost, postId);
        redirectAttributes.addFlashAttribute("postEditedSuccess", "Your post has been successfully edited!");
        return "redirect:/homepage/profile/{page}";
    }
    @PostMapping("/homepage/profile/{postId}/{page}/remove")
    public String removePost(@PathVariable(name = "postId") Long postId,
                             @PathVariable(name = "page") int page,
                             RedirectAttributes redirectAttributes){
        Post deletePost = postRepository.findPostById(postId);
        if(deletePost == null){
            return "redirect:/homepage/profile/{page}";
        }
        postRepository.delete(deletePost);
        redirectAttributes.addFlashAttribute("postRemovedSuccess", "Your post has been successfully removed!");
        return "redirect:/homepage/profile/{page}";
    }


    @GetMapping("/homepage/profile/edit")
    public String editUserProfile(Model model) {
        User user = userService.getAuthenticatedUser();
        if(user == null){
            return "redirect:/login";
        }
        if (user.getActivationCode() != null) {
            model.addAttribute("error_message", "Confirm your activation code!");
            return "redirect:/login";
        } else {
            user.setPassword("");
            user.setConfirmPassword("");
            model.addAttribute("user", user);
            return "/editUserProfile";
        }
    }

    @PostMapping("/homepage/profile/edit")
    public String updateUserProfile(@ModelAttribute("user") User user,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    @RequestParam(name = "fileImage") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setImage(fileName);
        userValidator.validate(user, bindingResult);
        Authentication authentication = userService.getAuthentication();
        if (bindingResult.hasErrors()) {
            User previousUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            user.setImage(previousUser.getImage());
            user.setId(previousUser.getId());
            return "editUserProfile";
        }

        User savedUser = userService.updateUser(user, authentication.getName());
        if (!StringUtils.isEmpty(fileName)) {
            String uploadDir = "./user-img/" + savedUser.getId();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                System.out.println("MYPATH = " + filePath.toFile().getAbsolutePath());
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        redirectAttributes.addFlashAttribute("updateSuccess", "Your account setting have been updated!");
        return "redirect:/homepage/profile";
    }

    @GetMapping("/homepage/newPost")
    public String newPostView(Model model, @ModelAttribute("newPost") Post newPost) {
        Authentication authentication = userService.getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName());
            model.addAttribute("user", user);
            return "newPost";
        }
        return "redirect:/login";
    }

    @PostMapping("/homepage/newPost")
    public String createPost(@RequestParam(value = "postDate", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate postDate,
                             @ModelAttribute("newPost") Post newPost,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        newPost.setPostDate(postDate);
        postValidator.validate(newPost, bindingResult);
        System.out.println("qweqeqweqweqeq" + bindingResult.getFieldError());
        if (bindingResult.hasErrors()) {
            Authentication authentication = userService.getAuthentication();
            model.addAttribute("user", userRepository.findByEmail(authentication.getName()));
            return "newPost";
        }
        Post savedPost = postService.savePost(newPost);
        redirectAttributes.addFlashAttribute("postCreatedSuccess", "Your post has been successfully created!");
        return "redirect:/homepage/newPost";
    }
}
