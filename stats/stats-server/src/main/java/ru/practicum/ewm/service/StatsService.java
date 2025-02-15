package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void hit(EndpointHitDto endpointHitDto);

    List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
