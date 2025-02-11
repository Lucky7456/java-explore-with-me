package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService service;

    @GetMapping("/requests")
    public List<ParticipationRequestDto> findAllByUserId(@PathVariable long userId) {
        return service.findAllByUserId(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @RequestParam Long eventId) {
        return service.create(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return service.cancel(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> findAllByEventId(@PathVariable long userId, @PathVariable long eventId) {
        return service.findAllByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult update(@PathVariable long userId,
                                                 @PathVariable long eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest request) {
        return service.update(userId, eventId, request);
    }
}
