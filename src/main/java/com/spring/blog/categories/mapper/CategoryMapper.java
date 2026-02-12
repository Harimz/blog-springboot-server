package com.spring.blog.categories.mapper;

import com.spring.blog.posts.domain.PostStatus;
import com.spring.blog.categories.dto.CategoryDto;
import com.spring.blog.categories.dto.CreateCategoryRequest;
import com.spring.blog.categories.dto.UpdateCategoryRequest;
import com.spring.blog.categories.Category;
import com.spring.blog.posts.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequest createCategoryRequest);

    Category toEntity(UpdateCategoryRequest updateCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if (null == posts) {
            return 0;
        }

        return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
