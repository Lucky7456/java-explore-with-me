package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    List<Request> findByRequesterId(long requesterId);

    Optional<Request> findByIdAndEventId(long id, long eventId);

    Optional<Request> findByEventId(long eventId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);
}
