package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsView;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatsService service;

    @PostMapping("/hit")
    public EndpointHitDto hit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return service.hit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<StatsView> getStats(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        if (end.isBefore(start)) {
            throw new ValidationException("End time can't be before start time");
        }
        return service.getStats(start, end, uris, unique);
    }
}
