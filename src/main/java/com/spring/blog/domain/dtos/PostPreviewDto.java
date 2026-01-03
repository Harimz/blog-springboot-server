package com.spring.blog.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPreviewDto {

    private UUID postId;

    private String title;

    private String coverImageUrl;

    private LocalDateTime createdAt;

    private CategoryDto category;
    private Set<TagResponse> tags;
    private AuthorDto author;
}
