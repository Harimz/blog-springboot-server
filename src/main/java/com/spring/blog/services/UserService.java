package com.spring.blog.services;

import com.spring.blog.domain.dtos.CreateUserRequest;
import com.spring.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {

    User createUser(CreateUserRequest request);

    User findUserById(UUID id);
}
