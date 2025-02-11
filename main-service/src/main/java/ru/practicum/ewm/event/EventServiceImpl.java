package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.client.stats.RestStatClient;
import ru.practicum.ewm.dto.StatsView;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RestStatClient statsClient;

    @Override
    public List<EventDto.Response.Private> findAllBy(List<Long> users, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, int from, int size) {
        List<Event> events = eventRepository.findAllBy(
                users,
                states,
                categories,
                start == null ? LocalDateTime.now() : start,
                end == null ? LocalDateTime.now().plusYears(1) : end,
                PageRequest.of(from, size)
        );
        return setConfirmedRequests(events).stream().map(EventMapper::mapToEventFullDto).toList();
    }

    @Override
    @Transactional
    public EventDto.Response.Private update(long eventId, EventDto.Request.AdminUpdate request) {
        Event event = eventRepository.findById(eventId).orElseThrow();

        if (request.getEventDate() != null && LocalDateTime.parse(request.getEventDate(), FORMATTER).isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Date must be no earlier than an hour from the date of publication");
        }

        if (request.getStateAction() == AdminActionState.PUBLISH_EVENT && event.getState() != EventState.PENDING) {
            throw new ConditionsNotMetException("Only pending events can be changed");
        }

        if (request.getStateAction() == AdminActionState.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Only unpublished events can be rejected");
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == AdminActionState.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (request.getStateAction() == AdminActionState.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }

        update(event, request);
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDto.Response.Private create(long userId, EventDto.Request.Create newEventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER);

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Remaining time is less than two hours.");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();

        Location location = locationRepository.save(LocationMapper.mapToLocation(newEventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.mapToEvent(newEventDto, user, category, location));
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public List<EventDto.Response.Public> findAllByUser(long userId, int from, int size) {
        return setConfirmedRequests(eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)))
                .stream().map(EventMapper::mapToShortEventDto).toList();
    }

    @Override
    public EventDto.Response.Private findById(long userId, long eventId) {
        userRepository.findById(userId).orElseThrow();
        return EventMapper.mapToEventFullDto(eventRepository.findById(eventId).orElseThrow());
    }

    @Override
    @Transactional
    public EventDto.Response.Private update(long userId, long eventId, EventDto.Request.UserUpdate request) {
        User initiator = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        if (initiator.getId() != event.getInitiator().getId()) {
            throw new ConditionsNotMetException("Only initiator can change the event.");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Only unpublished events can be changed.");
        }

        if (request.getEventDate() != null && LocalDateTime.parse(request.getEventDate(), FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Date must be no earlier than two hours from now.");
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == UserActionState.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
            if (request.getStateAction() == UserActionState.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        update(event, request);
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto.Response.Public> findAllBy(String text, List<Long> categories, Boolean paid, LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, EventSortState sort, int from, int size) {
        List<Event> events = eventRepository.findAllBy(
                text,
                categories,
                paid,
                start == null ? LocalDateTime.now() : start,
                end == null ? LocalDateTime.now().plusYears(100) : end,
                onlyAvailable,
                PageRequest.of(from, size)
        );

        Map<Long, Long> hits = getViewsToMap(setConfirmedRequests(events), true);
        List<EventDto.Response.Public> eventsWithViews = events.stream()
                .map(event -> EventMapper.mapToShortEventDto(event, hits.get(event.getId()))).toList();

        if (sort != null) {
            return switch (sort) {
                case VIEWS ->
                        eventsWithViews.stream().sorted(Comparator.comparing(EventDto.Response.Public::getViews)).toList();
                case EVENT_DATE ->
                        eventsWithViews.stream().sorted(Comparator.comparing(EventDto.Response.Public::getEventDate)).toList();
            };
        }

        return eventsWithViews;
    }

    @Override
    public EventDto.Response.Private findById(long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        if (event.getState() != EventState.PUBLISHED) {
            throw new NoSuchElementException("event unpublished");
        }
        event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED));
        long views = getViewsToMap(event.getPublishedOn(), LocalDateTime.now(), List.of("/events/" + id), true)
                .getOrDefault(id, 1L);
        return EventMapper.mapToEventFullDto(event, views);
    }

    private <T extends EventDto.Annotation & EventDto.Category & EventDto.Description & EventDto.EventDate &
            EventDto.Location & EventDto.Paid & EventDto.ParticipantLimit & EventDto.RequestModeration & EventDto.Title>
    void update(Event event, T dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory()).orElseThrow());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), FORMATTER));
        }
        if (dto.getLocation() != null) {
            event.setLocation(new Location(event.getLocation().getId(), dto.getLocation().lat(), dto.getLocation().lon()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }

    private List<Event> setConfirmedRequests(List<Event> events) {
        return events.stream().peek(event -> event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(
                event.getId(), RequestStatus.CONFIRMED))).toList();
    }

    private Map<Long, Long> getViewsToMap(List<Event> events, boolean unique) {
        List<String> uris = events.stream().map(elem -> String.format("/events/%d", elem.getId())).toList();
        LocalDateTime startDate = events.stream().map(Event::getPublishedOn).filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(LocalDateTime.now().minusYears(10));
        LocalDateTime endDate = events.stream().map(Event::getEventDate).filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(LocalDateTime.now().plusYears(100));
        return getViewsToMap(startDate, endDate, uris, unique);
    }

    private Map<Long, Long> getViewsToMap(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return statsClient.getStats(start, end, uris, unique).stream().collect(
                Collectors.toMap(event -> getIdFromUri(event.getUri()), StatsView::getHits)
        );
    }

    private Long getIdFromUri(String uri) {
        return uri.isBlank() ? 0 : Arrays.stream(uri.split("/")).skip(1)
                .filter(e -> e.chars().allMatch(Character::isDigit)).map(Long::valueOf).toList().getFirst();
    }
}
