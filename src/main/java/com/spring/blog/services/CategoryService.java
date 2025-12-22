package com.spring.blog.services;

import com.spring.blog.domain.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> listCategories();

    Category createCategory(Category category);

    void deleteCategory(UUID id);

    Category updateCategory(UUID id, Category updatedCategory);

    Category getCategoryById(UUID id);
}
