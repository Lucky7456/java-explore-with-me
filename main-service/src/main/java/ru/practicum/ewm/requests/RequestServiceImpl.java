package ru.practicum.ewm.requests;

import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllByUserId(long userId) {
        return requestRepository.findByRequesterId(userId).stream().map(RequestMapper::mapToDto).toList();
    }

    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User requester = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Request request = new Request(0L, event, requester, RequestStatus.PENDING, LocalDateTime.now());
        long limit = event.getParticipantLimit();

        validation(requester, event, limit);

        if (limit == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.mapToDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow();
        Request request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.mapToDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllByEventId(long userId, long eventId) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId);
        Event event = events.stream().filter(e -> e.getInitiator().getId() == userId).findFirst().orElseThrow();
        return requestRepository.findByEventId(event.getId()).stream().map(RequestMapper::mapToDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest eventRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow();
        long limit = event.getParticipantLimit();
        if (limit == 0 || !event.getRequestModeration()) {
            throw new NoSuchElementException();
        }

        List<Long> requestIds = eventRequest.getRequestIds();
        List<Request> requests = requestIds.stream()
                .map(r -> requestRepository.findByIdAndEventId(r, eventId).orElseThrow()).toList();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        long confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (confirmedRequestsCount >= limit) {
            throw new ConditionsNotMetException("Limit reached.");
        }

        List<Request> updatedRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getStatus() == RequestStatus.PENDING) {
                if (eventRequest.getStatus() == RequestStatus.CONFIRMED) {
                    if (confirmedRequestsCount <= limit) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        updatedRequests.add(request);
                        confirmedRequestsCount++;
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        updatedRequests.add(request);
                    }
                } else {
                    request.setStatus(eventRequest.getStatus());
                    updatedRequests.add(request);
                }
            }
        }

        List<Request> savedRequests = requestRepository.saveAll(updatedRequests);
        for (Request request : savedRequests) {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                confirmedRequests.add(RequestMapper.mapToDto(request));
            } else {
                rejectedRequests.add(RequestMapper.mapToDto(request));
            }
        }

        EventRequestStatusUpdateResult resultRequest = new EventRequestStatusUpdateResult();
        resultRequest.setConfirmedRequests(confirmedRequests);
        resultRequest.setRejectedRequests(rejectedRequests);
        return resultRequest;
    }

    private void validation(User user, Event event, long limit) {
        if (requestRepository.findByRequesterIdAndEventId(user.getId(), event.getId()).isPresent()) {
            throw new DuplicateKeyException("Request already exists.");
        }

        if (user.getId() == event.getInitiator().getId()) {
            throw new ConditionsNotMetException("Requester can't be event initiator.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Event unpublished.");
        }

        if (requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED) >= limit && limit != 0) {
            throw new ConditionsNotMetException("Limit reached.");
        }
    }
}
