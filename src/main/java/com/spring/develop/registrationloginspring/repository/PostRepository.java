package com.spring.develop.registrationloginspring.repository;

import com.spring.develop.registrationloginspring.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where pu.user_id=?",nativeQuery = true)
    Page<Post> findPostByUserId(Pageable  pageable, @Param("pu.user_id") Long id);

    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where posts.id in (select max(posts.id) from posts)",nativeQuery = true)
    Post findLastPost();

    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where pu.post_id not in (select max(posts.id) from posts)",nativeQuery = true)
    Page<Post> findAllPosts(Pageable pageable);

    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where posts.id in (select max(posts.id) from posts where posts.category = ?)",nativeQuery = true)
    Post findLastPostByTopic(@Param("posts.category") String topic);

    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where posts.category = ? and posts.id not in (select max(posts.id) from posts where posts.category = ?)",nativeQuery = true)
    Page<Post> findAllPostsByTopic(Pageable pageable,@Param("posts.category") String topic,@Param("posts.category") String topic2);

    @Query(value = "select * from posts inner join post_user pu on posts.id = pu.post_id where posts.id != ? and posts.category=?",nativeQuery = true)
    Page<Post> findAllPostsExceptIdCategory(Pageable pageable, @Param("posts.id") Long id,@Param("posts.category") String category);

    Post findPostById(Long id);

}
