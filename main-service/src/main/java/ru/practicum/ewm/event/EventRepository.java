package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event as e " +
            "where e.eventDate between :start and :end " +
            "and (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories)")
    List<Event> findAllBy(List<Long> users,
                          List<String> states,
                          List<Long> categories,
                          LocalDateTime start,
                          LocalDateTime end,
                          Pageable pageable);

    @Query("select e from Event as e " +
            "where e.eventDate between :start and :end " +
            "and (:text is null or (e.title ilike :text " +
            "or e.description ilike :text " +
            "or e.annotation ilike :text)) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (:available is null or e.state = 'PUBLISHED') " +
            "and (:categories is null or e.category.id in :categories)")
    List<Event> findAllBy(String text,
                          List<Long> categories,
                          Boolean paid,
                          LocalDateTime start,
                          LocalDateTime end,
                          Boolean available,
                          Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);
}
