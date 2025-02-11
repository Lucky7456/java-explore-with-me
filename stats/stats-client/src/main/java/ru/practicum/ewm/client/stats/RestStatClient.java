package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsView;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RestStatClient implements StatsClient {
    private final RestClient restClient;

    public RestStatClient(@Value("${stats.url}") String statUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statUrl)
                .build();
    }

    @Override
    public void hit(EndpointHitDto endpointHitDto) {
        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .onStatus(status -> status != HttpStatus.CREATED, ((request, response) -> {
                    throw new InvalidRequestException(response.getStatusCode().value() + ":" + response.getBody());
                }));
    }

    @Override
    public List<StatsView> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return restClient.get().uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.toString())
                        .queryParam("end", end.toString())
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, (request, response) -> {
                    throw new InvalidRequestException(response.getStatusCode().value() + ": " + response.getBody());
                })
                .body(ParameterizedTypeReference.forType(List.class));
    }
}
