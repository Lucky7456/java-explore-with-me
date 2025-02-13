package ru.practicum.ewm.requests;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findAllByUserId(long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> findAllByEventId(long userId, long eventId);

    EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest eventRequest);
}
