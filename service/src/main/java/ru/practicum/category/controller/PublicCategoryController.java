package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories(
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from) {
        log.info("Получение категорий");
        return categoryService.getAllCategories(size, from);
    }

    @GetMapping("/{catId}")
    public Category getCategoryById(@PathVariable Long catId) {
        Category category = categoryService.getCategoryById(catId);
        log.info("Получение информации о категории по ее id {}", catId);
        return category;
    }
}
