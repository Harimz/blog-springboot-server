package com.spring.blog.controllers;

import com.spring.blog.domain.CreatePostRequest;
import com.spring.blog.domain.UpdatePostRequest;
import com.spring.blog.domain.dtos.CreatePostRequestDto;
import com.spring.blog.domain.dtos.PostDto;
import com.spring.blog.domain.dtos.UpdatePostRequestDto;
import com.spring.blog.domain.entities.Post;
import com.spring.blog.domain.entities.User;
import com.spring.blog.mappers.PostMapper;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.services.PostService;
import com.spring.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId
    ) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDto> postDtos = posts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@AuthenticationPrincipal BlogUserDetails currentUser) {
        User user = currentUser.getUser();

        List<Post> draftPosts = postService.getDraftPosts(user);
        List<PostDto> postDtos = draftPosts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
            @AuthenticationPrincipal BlogUserDetails currentUser
    ) {
        User user = currentUser.getUser();

        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(user, createPostRequest);
        PostDto createdPostDto = postMapper.toDto(createdPost);

        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto,
            @AuthenticationPrincipal BlogUserDetails currentUser
    ) {
        User user = currentUser.getUser();

        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);

        Post updatedPost = postService.updatePost(id, user, updatePostRequest);
        PostDto updatedPostDto = postMapper.toDto(updatedPost);

        return new ResponseEntity<>(updatedPostDto, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
        Post post = postService.getPost(id);
        PostDto postDto = postMapper.toDto(post);
        return ResponseEntity.ok(postDto);
    }
}
