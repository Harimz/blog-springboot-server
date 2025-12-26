package com.spring.blog.services;

import com.spring.blog.domain.dtos.AuthResponse;
import com.spring.blog.security.BlogUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    record RefreshResult(String accessToken, long expiresIn, String refreshToken) {}

    UserDetails authenticate(String email, String password);

    String generateAccessToken(UserDetails userDetails);
    UserDetails validateAccessToken(String token);

    String issueOrReplaceRefreshToken(BlogUserDetails userDetails);
    void logout(String refreshToken);

    RefreshResult refresh(String refreshToken);
}
