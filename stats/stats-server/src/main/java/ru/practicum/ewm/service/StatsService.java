package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto hit(EndpointHitDto endpointHitDto);

    List<StatsView> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
