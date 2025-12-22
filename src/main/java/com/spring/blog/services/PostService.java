package com.spring.blog.services;

import com.spring.blog.domain.CreatePostRequest;
import com.spring.blog.domain.UpdatePostRequest;
import com.spring.blog.domain.entities.Post;
import com.spring.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {

    Post getPost(UUID id);

    List<Post> getAllPosts(UUID categoryId, UUID tagId);

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID postId, User user, UpdatePostRequest updatePostRequest);
}
