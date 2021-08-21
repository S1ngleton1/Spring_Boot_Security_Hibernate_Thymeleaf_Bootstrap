package com.spring.develop.registrationloginspring.service;

import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.PostRepository;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Override
    public Post savePost(Post newPost) {
        User user = userService.getAuthenticatedUser();
        Set<User> users = new HashSet<>();
        users.add(userRepository.findUserById(user.getId()));//Get User ID
        newPost.setUsers(users);
        postRepository.save(newPost);
        return newPost;
    }

    @Override
    public Page<Post> findPaginadedByUserId(int pageNumber, int pageSize, Long id) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        return this.postRepository.findPostByUserId(pageable,id);
    }

    @Override
    public Post updatePost(Post updatedPost, Long id) {
        Post newPost = postRepository.findPostById(id);
        newPost.setTitle(updatedPost.getTitle());
        newPost.setShortDescription(updatedPost.getShortDescription());
        newPost.setFullDescription(updatedPost.getFullDescription());
        newPost.setCategory(updatedPost.getCategory());
        newPost.setPostDate(updatedPost.getPostDate());
        postRepository.save(newPost);
        return newPost;
    }

    @Override
    public Page<Post> findPaginated(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        return this.postRepository.findAllPosts(pageable);
    }
    @Override
    public Page<Post> findPaginatedExceptIdCategory(int pageNumber, int pageSize, Long id, String category) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        return this.postRepository.findAllPostsExceptIdCategory(pageable, id, category);
    }
    @Override
    public Page<Post> findTopicPaginated(int pageNumber, int pageSize, String topic) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        return this.postRepository.findAllPostsByTopic(pageable,topic, topic);
    }


}
