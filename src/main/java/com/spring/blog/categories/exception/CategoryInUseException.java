package com.spring.blog.categories.exception;

import java.util.UUID;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(UUID id) {
        super("Category with id " + id + " is already in use");
    }
}
