package ru.practicum.ewm.compilation;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findAllBy(Boolean pinned, int from, int size);

    CompilationDto findById(long compId);

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest);

    void delete(long compId);
}
