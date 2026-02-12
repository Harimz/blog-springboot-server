package com.spring.blog.auth.service;

import com.spring.blog.auth.dto.CreateUserRequest;
import com.spring.blog.auth.User;

import java.util.UUID;

public interface UserService {

    User createUser(CreateUserRequest request);

    User findUserById(UUID id);

    User updateAvatar(UUID userId, String avatarUrl);
}
