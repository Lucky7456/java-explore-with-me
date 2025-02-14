package ru.practicum.ewm.client.stats;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsViewDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .registerModule(new JavaTimeModule());

    public StatsClient(@Value("${stats.url:http://localhost:9090}") String serverUrl, RestTemplateBuilder builder) {
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

    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        ));

        try {
            return mapper.readValue(mapper.writeValueAsString(response.getBody()), mapper.getTypeFactory()
                    .constructCollectionType(List.class, StatsViewDto.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
