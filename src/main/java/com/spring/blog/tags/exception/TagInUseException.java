package com.spring.blog.tags.exception;

import java.util.UUID;

public class TagInUseException extends RuntimeException {

    public TagInUseException(UUID id) {
        super("Cannot delete tag with id " + id + " because it is associated with posts");
    }
}
