package com.spring.blog.services.impl;

import com.spring.blog.domain.dtos.AuthResponse;
import com.spring.blog.domain.entities.RefreshToken;
import com.spring.blog.domain.entities.User;
import com.spring.blog.repositories.RefreshTokenRepository;
import com.spring.blog.repositories.UserRepository;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${refresh.pepper}")
    private String refreshPepper;

    @Value("${auth.access.expiry-ms:900000}")
    private Long accessExpiryMs;

    @Value("${auth.refresh.days:14}")
    private int refreshDays;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public UserDetails authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateAccessToken(String token) {
        String username = extractUsername(token);

        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public String issueOrReplaceRefreshToken(BlogUserDetails userDetails) {
        String raw = newRefreshToken();
        String hash = sha256Hex(raw + refreshPepper);

        Instant expiresAt = Instant.now().plus(Duration.ofDays(refreshDays));

        refreshTokenRepository.findByUserId(userDetails.getId()).ifPresent(existing -> {
            existing.setTokenHash(hash);
            existing.setExpiresAt(expiresAt);
            refreshTokenRepository.save(existing);
        });

        if (refreshTokenRepository.findByUserId(userDetails.getId()).isEmpty()) {
            RefreshToken rt = RefreshToken.builder()
                    .userId(userDetails.getId())
                    .tokenHash(hash)
                    .expiresAt(expiresAt)
                    .createdAt(Instant.now())
                    .build();
            refreshTokenRepository.save(rt);
        }

        return raw;
    }

    @Override
    public RefreshResult refresh(String refreshToken) {
        String hash = sha256Hex(refreshToken + refreshPepper);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (Instant.now().isAfter(existing.getExpiresAt())) {
            refreshTokenRepository.deleteById(existing.getId());
            throw new RuntimeException("Refresh token expired");
        }

        User user = userRepository.findById(existing.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String newRaw = newRefreshToken();
        existing.setTokenHash(sha256Hex(newRaw + refreshPepper));
        existing.setExpiresAt(Instant.now().plus(Duration.ofDays(refreshDays)));
        refreshTokenRepository.save(existing);

        String newAccess = generateAccessToken(userDetails);

        return new RefreshResult(newAccess, accessExpiryMs / 1000, newRaw);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;

        String hash = sha256Hex(refreshToken + refreshPepper);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt ->
                refreshTokenRepository.deleteById(rt.getId())
        );
    }

    // Helpers

    private Key getSigningKey() {
         byte[] keyBytes =  secretKey.getBytes();

         return Keys.hmacShaKeyFor(keyBytes);
    }

    private String extractUsername(String token) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
    }

    private String newRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
