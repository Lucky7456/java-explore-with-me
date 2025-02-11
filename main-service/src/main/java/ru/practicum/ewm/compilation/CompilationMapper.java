package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.EventMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents() != null ? compilation.getEvents().stream().map(EventMapper::mapToShortEventDto).toList() : null,
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
