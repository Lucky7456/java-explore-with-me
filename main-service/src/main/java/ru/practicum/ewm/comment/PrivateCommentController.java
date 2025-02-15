package ru.practicum.ewm.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService service;

    @GetMapping
    public List<CommentDto> findAllByUser(@PathVariable long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return service.findAllByUser(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@PathVariable long userId,
                               @PathVariable long commentId) {
        return service.findById(userId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable long userId,
                             @RequestParam Long eventId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        return service.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long commentId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        return service.update(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId,
                       @PathVariable long commentId) {
        service.delete(userId, commentId);
    }
}
