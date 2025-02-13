package ru.practicum.ewm.event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventDto.Response.Private> findAllBy(List<Long> users, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, int from, int size);

    EventDto.Response.Private update(long eventId, EventDto.Request.AdminUpdate updateEventAdminRequest);

    EventDto.Response.Private create(long userId, EventDto.Request.Create newEventDto);

    List<EventDto.Response.Public> findAllByUser(long userId, int from, int size);

    EventDto.Response.Private findById(long userId, long eventId);

    EventDto.Response.Private update(long userId, long eventId, EventDto.Request.UserUpdate updateEventUserRequest);

    List<EventDto.Response.Public> findAllBy(String text, List<Long> categories, Boolean paid, LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, EventSortState sort, int from, int size);

    EventDto.Response.Private findById(long id);
}
