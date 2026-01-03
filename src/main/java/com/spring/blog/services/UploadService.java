package com.spring.blog.services;

import com.spring.blog.domain.dtos.PresignUploadRequest;
import com.spring.blog.domain.dtos.PresignUploadResponse;

import java.util.UUID;

public interface UploadService {
    PresignUploadResponse presignPostCover(UUID userId, PresignUploadRequest req);
}
