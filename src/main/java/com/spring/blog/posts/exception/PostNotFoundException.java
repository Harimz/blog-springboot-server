package com.spring.blog.posts.exception;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(UUID id) {
        super("Post does not exist with id: " + id);
    }
}
