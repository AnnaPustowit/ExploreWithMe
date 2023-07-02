package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationsService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto);

    List<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilationsById(Long compId);

    void deleteCompilationById(Long compId);
}
