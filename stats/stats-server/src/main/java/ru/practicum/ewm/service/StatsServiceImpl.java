package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsViewDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    @Transactional
    public void hit(EndpointHitDto endpointHitDto) {
        EndpointHitMapper.toEndpointHitDto(repository.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return repository.findAllUniqueIpAndTimestampBetweenAndUriIn(start, end, uris);
        }
        return repository.findAllByTimestampBetweenAndUriIn(start, end, uris);
    }
}
