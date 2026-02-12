package com.spring.blog.posts.web;

import com.spring.blog.posts.dto.*;
import com.spring.blog.posts.Post;
import com.spring.blog.auth.User;
import com.spring.blog.posts.mapper.PostMapper;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.posts.service.PostService;
import com.spring.blog.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<PostPreviewDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Post> posts = postService.getAllPosts(categoryId, tagId, pageable);

        Page<PostPreviewDto> dtoPage = posts.map(postMapper::toPreviewDto);

        return ResponseEntity.ok(dtoPage);
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
            @Valid @RequestBody UpdatePostContentRequestDTO dto,
            @AuthenticationPrincipal BlogUserDetails currentUser
    ) {
        User user = currentUser.getUser();

        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(dto);

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

    @GetMapping(path = "/myPosts")
    public ResponseEntity<List<PostPreviewDto>> getMyPosts(@AuthenticationPrincipal BlogUserDetails currentUser
    ) {
        UUID userId = currentUser.getUser().getId();

        List<Post> posts = postService.getMyPosts(userId);
        List<PostPreviewDto> postPreviewDtos = posts.stream()
                .map(postMapper::toPreviewDto)
                .toList();

        return ResponseEntity.ok(postPreviewDtos);
    }
}
