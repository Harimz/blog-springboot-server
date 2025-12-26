package com.spring.blog.controllers;

import com.spring.blog.domain.dtos.AuthResponse;
import com.spring.blog.domain.dtos.LoginRequest;
import com.spring.blog.domain.dtos.CreateUserRequest;
import com.spring.blog.domain.dtos.UserResponse;
import com.spring.blog.domain.entities.User;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.services.AuthenticationService;
import com.spring.blog.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Value("${app.cookies.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookies.sameSite:Lax}")
    private String cookieSameSite;

    @Value("${app.cookies.refreshName:refresh_token}")
    private String refreshCookieName;

    private final Duration refreshCookieMaxAge = Duration.ofDays(4);

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserDetails userDetails = authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        String accessToken = authenticationService.generateAccessToken(userDetails);

        String refreshToken = authenticationService.issueOrReplaceRefreshToken((BlogUserDetails) userDetails);

        setRefreshCookie(response, refreshToken);

        AuthResponse authResponse = AuthResponse.builder()
                .token(accessToken)
                .expiresIn(900)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "${app.cookies.refreshName:refresh_token}", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthenticationService.RefreshResult refreshed = authenticationService.refresh(refreshToken);

        setRefreshCookie(response, refreshed.refreshToken());

        AuthResponse authResponse = AuthResponse.builder()
                .token(refreshed.accessToken())
                .expiresIn(refreshed.expiresIn())
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "${app.cookies.refreshName:refresh_token}", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.logout(refreshToken);
        }

        clearRefreshCookie(response);

        return ResponseEntity.noContent().build();
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
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/v1/auth")
                .maxAge(refreshCookieMaxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
