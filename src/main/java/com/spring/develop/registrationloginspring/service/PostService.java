package com.spring.develop.registrationloginspring.service;

import com.spring.develop.registrationloginspring.models.Post;
import org.springframework.data.domain.Page;

public interface PostService {
    Post savePost(Post newPost);
    Page<Post> findPaginadedByUserId(int pageNumber, int pageSize, Long id);
    Post updatePost(Post updatedPost, Long id);
    Page<Post> findPaginated(int pageNumber, int pageSize);
    Page<Post> findPaginatedExceptIdCategory(int pageNumber, int pageSize, Long id, String category);
    Page<Post> findTopicPaginated(int pageNumber, int pageSize, String topic);
}
