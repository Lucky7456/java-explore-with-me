package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final EventService service;

    @GetMapping
    public List<EventDto.Response.Private> adminFindAll(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.findAllBy(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto.Response.Private adminUpdate(@PathVariable long eventId,
                                                 @Valid @RequestBody EventDto.Request.AdminUpdate updateEventAdminRequest) {
        return service.update(eventId, updateEventAdminRequest);
    }
}
