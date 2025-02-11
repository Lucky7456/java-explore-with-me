package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findAllBy(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream().map(CompilationMapper::toCompilationDto).toList();
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable).stream().map(CompilationMapper::toCompilationDto).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findById(long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compId).orElseThrow());
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation(0L, null, newCompilationDto.isPinned(), newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(long compId) {
        compilationRepository.findById(compId).orElseThrow();
        compilationRepository.deleteById(compId);
    }
}
