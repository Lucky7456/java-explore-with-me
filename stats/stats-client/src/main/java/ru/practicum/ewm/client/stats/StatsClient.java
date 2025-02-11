package ru.practicum.ewm.client.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    public StatsClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public void hit(EndpointHitDto endpointHitDto) {
        post(endpointHitDto);
    }

    public List<StatsView> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .convertValue(
                        get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                                Map.of("start", start, "end", end, "uris", String.join(", ", uris), "unique", unique)
                        ).getBody(),
                        new TypeReference<>() {}
                );
    }
}
