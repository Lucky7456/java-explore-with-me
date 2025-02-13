package ru.practicum.ewm.comment;

import ru.practicum.ewm.event.EventDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

public record CommentDto(long id, String text, UserShortDto author, EventDto.Response.Public event,
                         LocalDateTime created) {
}
