package com.spring.blog.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignUploadResponse {

    private String key;
    private String uploadUrl;
    private String publicUrl;
}
