package com.spring.blog.auth.web;

import com.spring.blog.auth.dto.*;
import com.spring.blog.auth.User;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.auth.service.AuthenticationService;
import com.spring.blog.auth.service.UserService;
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
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserDetails userDetails = authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        String accessToken = authenticationService.generateAccessToken(userDetails);

        String refreshToken = authenticationService.issueOrReplaceRefreshToken((BlogUserDetails) userDetails);

        setRefreshCookie(response, refreshToken);

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(accessToken)
                .expiresIn(900)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthenticationService.RefreshResult refreshed = authenticationService.refresh(refreshToken);

        setRefreshCookie(response, refreshed.refreshToken());

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(refreshed.accessToken())
                .expiresIn(refreshed.expiresIn())
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
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
                .avatarUrl(user.getAvatarUrl())
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

    @PutMapping("/me/avatar")
    public ResponseEntity<UserResponse> updateMyAvatar(
            @AuthenticationPrincipal BlogUserDetails currentUser,
            @Valid @RequestBody UpdateAvatarRequest req
    ) {
        User updated = userService.updateAvatar(currentUser.getId(), req.getAvatarUrl());

        UserResponse response = UserResponse.builder()
                .id(updated.getId())
                .email(updated.getEmail())
                .name(updated.getName())
                .avatarUrl(updated.getAvatarUrl())
                .createdAt(updated.getCreatedAt())
                .role(updated.getRole())
                .build();

        return ResponseEntity.ok(response);
    }
}
