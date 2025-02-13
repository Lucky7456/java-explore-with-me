package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService service;

    @GetMapping("/events/{eventId}")
    public List<CommentDto> findAllByEvent(@PathVariable long eventId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return service.findAllByEvent(eventId, from, size);
    }

    @GetMapping
    public List<CommentDto> findAllRecent(@RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return service.findAllRecent(from, size);
    }
}
