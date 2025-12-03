package com.spring.blog.services.impl;

import com.spring.blog.domain.entities.Category;
import com.spring.blog.domain.exceptions.categories.CategoryAlreadyExistsException;
import com.spring.blog.domain.exceptions.categories.CategoryInUseException;
import com.spring.blog.domain.exceptions.categories.CategoryNotFoundException;
import com.spring.blog.repositories.CategoryRepository;
import com.spring.blog.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
}
