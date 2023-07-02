package ru.practicum.category.service;

import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    List<Category> getAllCategories(Integer from, Integer size);

    Category getCategoryById(Long id);

    Category updateCategory(Long id, Category category);

    void deleteCategoryById(Long id);
}
