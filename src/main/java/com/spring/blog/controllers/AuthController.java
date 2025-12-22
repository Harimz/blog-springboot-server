package com.spring.blog.controllers;

import com.spring.blog.domain.dtos.AuthResponse;
import com.spring.blog.domain.dtos.LoginRequest;
import com.spring.blog.domain.dtos.CreateUserRequest;
import com.spring.blog.domain.dtos.UserResponse;
import com.spring.blog.domain.entities.User;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.services.AuthenticationService;
import com.spring.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        String tokenValue = authenticationService.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal BlogUserDetails currentUser) {
        var user = currentUser.getUser();

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getEmail())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
