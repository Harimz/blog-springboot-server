package com.spring.blog.upload.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PresignUploadRequest {

    @NotBlank
    private String filename;

    @NotBlank
    private String contentType;

    private String kind;
}
