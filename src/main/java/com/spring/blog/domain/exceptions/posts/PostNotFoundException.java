package com.spring.blog.domain.exceptions.posts;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(UUID id) {
        super("Post does not exist with id: " + id);
    }
}
