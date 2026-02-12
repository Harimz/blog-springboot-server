package com.spring.blog.categories.service.impl;

import com.spring.blog.categories.Category;
import com.spring.blog.categories.exception.CategoryAlreadyExistsException;
import com.spring.blog.categories.exception.CategoryInUseException;
import com.spring.blog.categories.exception.CategoryNotFoundException;
import com.spring.blog.categories.persistence.CategoryRepository;
import com.spring.blog.categories.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if(categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category already exists with name: " + category.getName());
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getPosts().isEmpty()) {
            throw new CategoryInUseException(id);
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, Category updatedCategory) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(updatedCategory.getName(), id)) {
            throw new CategoryAlreadyExistsException(updatedCategory.getName());
        }

        existing.setName(updatedCategory.getName());

        return categoryRepository.save(existing);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
