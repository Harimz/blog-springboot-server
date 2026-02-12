package com.spring.blog.categories.service;

import com.spring.blog.categories.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> listCategories();

    Category createCategory(Category category);

    void deleteCategory(UUID id);

    Category updateCategory(UUID id, Category updatedCategory);

    Category getCategoryById(UUID id);
}
