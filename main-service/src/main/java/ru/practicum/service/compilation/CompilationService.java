package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    public CompilationDto createCompilation(NewCompilationDto dto);

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

    public CompilationDto getCompilationById(Long compId);

    public void deleteCompilation(Long compId);
}
