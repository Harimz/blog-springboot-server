package com.spring.blog.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignUploadResponse {

    private String key;
    private String uploadUrl;
    private String publicUrl;
}
