package com.spring.blog.domain.dtos;

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
