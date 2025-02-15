package ru.practicum.ewm.comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<CommentDto> findAllBy(List<Long> users, List<Long> events, LocalDateTime start, LocalDateTime end, int from, int size);

    List<CommentDto> findAllByEvent(long eventId, int from, int size);

    List<CommentDto> findAllRecent(int from, int size);

    List<CommentDto> findAllByUser(long userId, int from, int size);

    CommentDto findById(long userId, long commentId);

    CommentDto create(long userId, long eventId, NewCommentDto commentDto);

    CommentDto update(long userId, long commentId, NewCommentDto commentDto);

    void delete(long userId, long commentId);

    void delete(long commentId);
}
