package ru.practicum.ewm.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                UserMapper.mapToShortUserDto(comment.getAuthor()),
                EventMapper.mapToShortEventDto(comment.getEvent()),
                comment.getCreated()
        );
    }

    public static Comment toComment(User author, Event event, NewCommentDto newCommentDto) {
        return new Comment(0L, newCommentDto.text(), author, event, LocalDateTime.now());
    }
}
