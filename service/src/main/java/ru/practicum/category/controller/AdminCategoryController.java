package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryServiceImpl categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody @Valid Category category) {
        Category newCategory = categoryService.createCategory(category);
        log.info("Добавление новой категории: {}", category);
        return newCategory;
    }

    @PatchMapping("/{id}")
    public Category updateCategory(@Positive @PathVariable Long id,
                                   @RequestBody @Valid Category category) {
        log.info("Изменение категории по id: {}", id);
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@Positive @PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        log.info("Удаление категории по id: {}", id);
    }
}
