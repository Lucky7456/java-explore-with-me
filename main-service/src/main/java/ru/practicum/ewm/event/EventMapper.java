package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventDto.Response.Private mapToEventFullDto(Event event) {
        return mapToEventFullDto(event, 0);
    }

    public static EventDto.Response.Private mapToEventFullDto(Event event, long views) {
        return new EventDto.Response.Private(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate().format(FORMATTER),
                UserMapper.mapToShortUserDto(event.getInitiator()),
                LocationMapper.mapToLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views
        );
    }

    public static Event mapToEvent(EventDto.Request.Create newEventDto, User user, Category category, Location location) {
        return new Event(
                0L,
                newEventDto.getAnnotation(),
                category,
                0L,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER),
                user,
                location,
                newEventDto.getPaid() != null && newEventDto.getPaid(),
                newEventDto.getParticipantLimit() == null ? 0L : newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle()
        );
    }

    public static EventDto.Response.Public mapToShortEventDto(Event event) {
        return mapToShortEventDto(event, 0L, 0L);
    }

    public static EventDto.Response.Public mapToShortEventDto(Event event, Long views, Long CommentCount) {
        return new EventDto.Response.Public(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(FORMATTER),
                UserMapper.mapToShortUserDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views,
                CommentCount
        );
    }
}
