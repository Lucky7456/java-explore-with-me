package ru.practicum.ewm.client.stats;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void hit(EndpointHitDto endpointHitDto);

    List<StatsView> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
