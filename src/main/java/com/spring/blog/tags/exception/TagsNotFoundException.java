package com.spring.blog.tags.exception;

public class TagsNotFoundException extends RuntimeException {
    public TagsNotFoundException() {
        super("Not all specified tag IDs exist");
    }
}
