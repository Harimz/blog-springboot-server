package com.spring.blog.posts.service;

import com.spring.blog.posts.dto.CreatePostRequest;
import com.spring.blog.posts.dto.UpdatePostRequest;
import com.spring.blog.posts.Post;
import com.spring.blog.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    Post getPost(UUID id);

    Page<Post> getAllPosts(UUID categoryId, UUID tagId, Pageable pageable);

    List<Post> getMyPosts(UUID userId);

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID postId, User user, UpdatePostRequest updatePostRequest);
}
