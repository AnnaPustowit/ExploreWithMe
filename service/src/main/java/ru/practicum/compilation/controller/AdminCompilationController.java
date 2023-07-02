package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationsService;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = compilationsService.createCompilation(newCompilationDto);
        log.info("Добавление новой подборки событий {}", newCompilationDto);
        return compilationDto;
    }

    @PatchMapping("/{id}")
    public CompilationDto updateCompilation(@PathVariable Long id,
                                            @Valid @RequestBody NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = compilationsService.updateCompilation(id, newCompilationDto);
        log.info("Обновление информации о подборки событий по ее id {}", id);
        return compilationDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long id) {
        compilationsService.deleteCompilationById(id);
        log.info("Удаление подборки событий по ее id {}", id);
    }
}
