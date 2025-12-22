package com.spring.blog.security;

import com.spring.blog.repositories.PostRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("postSecurity")
@RequiredArgsConstructor
public class PostSecurity {

    private final PostRepository postRepository;

    public boolean isOwner(UUID postId, Authentication authentication) {
        BlogUserDetails userDetails = (BlogUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        return postRepository.findById(postId)
                .map(post -> post.getAuthor().getId().equals(userId))
                .orElse(false);
    }
}
