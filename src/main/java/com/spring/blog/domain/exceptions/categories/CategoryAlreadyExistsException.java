package com.spring.blog.domain.exceptions.categories;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String name) {
        super("Category already exists with name: " + name);
    }
}
