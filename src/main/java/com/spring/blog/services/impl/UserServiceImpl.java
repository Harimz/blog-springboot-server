package com.spring.blog.services.impl;

import com.spring.blog.domain.UserRole;
import com.spring.blog.domain.dtos.CreateUserRequest;
import com.spring.blog.domain.entities.User;
import com.spring.blog.domain.exceptions.users.UserAlreadyExistsException;
import com.spring.blog.domain.exceptions.users.UserNotFoundException;
import com.spring.blog.repositories.UserRepository;
import com.spring.blog.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
