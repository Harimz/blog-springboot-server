package com.spring.blog.upload.service;

import com.spring.blog.upload.dto.PresignUploadRequest;
import com.spring.blog.upload.dto.PresignUploadResponse;

import java.util.UUID;

public interface UploadService {
    PresignUploadResponse presignPostCover(UUID userId, PresignUploadRequest req);

    PresignUploadResponse presignUserAvatar(UUID userId, PresignUploadRequest req);
}
