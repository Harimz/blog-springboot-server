package com.spring.blog.controllers.exceptions;

import com.spring.blog.controllers.AuthController;
import com.spring.blog.controllers.TagController;
import com.spring.blog.domain.dtos.ApiErrorResponse;
import com.spring.blog.domain.exceptions.tags.TagInUseException;
import com.spring.blog.domain.exceptions.tags.TagNotFoundException;
import com.spring.blog.domain.exceptions.tags.TagsNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = TagController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TagExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTagNotFoundException(TagNotFoundException e) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTagsNotFoundException(TagsNotFoundException e) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagInUseException.class)
    public ResponseEntity<ApiErrorResponse> handleTagInUseException(TagInUseException e) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
