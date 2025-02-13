package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto.Response.Private create(@PathVariable long userId,
                                            @Valid @RequestBody EventDto.Request.Create newEventDto) {
        return service.create(userId, newEventDto);
    }

    @GetMapping
    public List<EventDto.Response.Public> findAllByUser(@PathVariable long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        return service.findAllByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto.Response.Private findById(@PathVariable long userId,
                                              @PathVariable long eventId) {
        return service.findById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto.Response.Private update(@PathVariable long userId,
                                            @PathVariable long eventId,
                                            @Valid @RequestBody EventDto.Request.UserUpdate updateEventUserRequest) {
        return service.update(userId, eventId, updateEventUserRequest);
    }
}
