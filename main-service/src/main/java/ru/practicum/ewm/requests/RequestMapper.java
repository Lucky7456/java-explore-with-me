package ru.practicum.ewm.requests;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {
    public static ParticipationRequestDto mapToDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
