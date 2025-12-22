package com.spring.blog.domain.exceptions.tags;

public class TagsNotFoundException extends RuntimeException {
    public TagsNotFoundException() {
        super("Not all specified tag IDs exist");
    }
}
