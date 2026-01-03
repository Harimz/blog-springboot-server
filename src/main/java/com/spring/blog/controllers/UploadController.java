package com.spring.blog.controllers;

import com.spring.blog.domain.dtos.PresignUploadRequest;
import com.spring.blog.domain.dtos.PresignUploadResponse;
import com.spring.blog.security.BlogUserDetails;
import com.spring.blog.services.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/presign/post-cover")
    public ResponseEntity<PresignUploadResponse> presignPostCover(
            @Valid @RequestBody PresignUploadRequest req,
            @AuthenticationPrincipal BlogUserDetails currentUser
    ) {
        var userId = currentUser.getId();
        return ResponseEntity.ok(uploadService.presignPostCover(userId, req));
    }
}
