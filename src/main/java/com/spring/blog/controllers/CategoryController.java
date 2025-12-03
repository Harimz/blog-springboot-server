package com.spring.blog.controllers;

import com.spring.blog.domain.dtos.CategoryDto;
import com.spring.blog.domain.dtos.CreateCategoryRequest;
import com.spring.blog.domain.dtos.UpdateCategoryRequest;
import com.spring.blog.domain.entities.Category;
import com.spring.blog.mappers.CategoryMapper;
import com.spring.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categories = categoryService.listCategories()
                .stream()
                .map(categoryMapper::toDto).toList();

        return ResponseEntity.ok(categories);
    }


    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category categoryToCreate = categoryMapper.toEntity(createCategoryRequest);
        Category savedCategory = categoryService.createCategory(categoryToCreate);
        return new ResponseEntity<>(
                categoryMapper.toDto(savedCategory),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest
    ) {
        Category updatedData = categoryMapper.toEntity(updateCategoryRequest);
        Category updated = categoryService.updateCategory(id, updatedData);
        CategoryDto updatedCategory = categoryMapper.toDto(updated);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
