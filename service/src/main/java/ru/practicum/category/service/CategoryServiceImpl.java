package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.exeption.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public Category createCategory(Category category) {
        if (category.getName().isBlank() || category.getName().isEmpty()) {
            throw new InvalidParameterException("У категории должно быть название.");
        }
        if (categoryRepository.existsCategoryByName(category.getName())) {
            throw new ConflictException("Уже есть категория с названием: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {
        return categoryRepository.getAllWithPagination(from, size);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет категории с id: " + categoryId);
        }
        return category.get();
    }

    @Override
    public Category updateCategory(Long categoryId, Category updateCategory) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет категории с id: " + categoryId);
        }
        if (categoryRepository.existsCategoryByName(updateCategory.getName()) && !category.get().getName().equals(updateCategory.getName())) {
            throw new ConflictException("Уже есть категория с названием: " + updateCategory.getName());
        }
        category.get().setName(updateCategory.getName());
        return categoryRepository.save(category.get());
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет категории с id: " + categoryId);
        }
        if (eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new ConflictException("Нельзя удалить категорию с id: " + categoryId);
        }
    }
}
