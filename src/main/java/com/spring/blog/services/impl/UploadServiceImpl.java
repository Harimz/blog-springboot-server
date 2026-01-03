package com.spring.blog.services.impl;

import com.spring.blog.domain.dtos.PresignUploadRequest;
import com.spring.blog.domain.dtos.PresignUploadResponse;
import com.spring.blog.services.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final S3Presigner presigner;

    @Value("${r2.bucket}")
    private String bucket;

    @Value("${r2.publicBaseUrl}")
    private String publicBaseUrl;

    @Override
    public PresignUploadResponse presignPostCover(UUID userId, PresignUploadRequest req) {
        if (req.getContentType() == null || !req.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed");
        }

        String ext = getExtension(req.getFilename());
        String key = "posts/" + userId + "/covers/" + UUID.randomUUID() + (ext.isBlank() ? "" : "." + ext);


        var putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(req.getContentType())
                .build();

        var presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putReq)
                .build();

        var signed = presigner.presignPutObject(presignReq);

        String publicUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl + key
                : publicBaseUrl + "/" + key;

        return new PresignUploadResponse(key, signed.url().toString(), publicUrl);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        if (i < 0 || i == filename.length() - 1) return "";
        return filename.substring(i + 1).toLowerCase();
    }
}
