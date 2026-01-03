package com.spring.blog.services;

import com.spring.blog.domain.CreatePostRequest;
import com.spring.blog.domain.UpdatePostRequest;
import com.spring.blog.domain.entities.Post;
import com.spring.blog.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    Post getPost(UUID id);

    Page<Post> getAllPosts(UUID categoryId, UUID tagId, Pageable pageable);

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID postId, User user, UpdatePostRequest updatePostRequest);
}
