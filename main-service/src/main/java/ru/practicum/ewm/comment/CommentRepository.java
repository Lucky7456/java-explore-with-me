package ru.practicum.ewm.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment as c " +
            "where c.created between :start and :end " +
            "and (:users is null or c.author.id in :users) " +
            "and (:events is null or c.event.id in :events)")
    List<Comment> findAllBy(List<Long> users,
                            List<Long> events,
                            LocalDateTime start,
                            LocalDateTime end,
                            Pageable pageable);

    Optional<Comment> findByIdAndAuthorId(long commentId, long userId);

    List<Comment> findAllByAuthorId(long userId, Pageable pageable);

    List<Comment> findAllByEventId(long eventId, Pageable pageable);

    List<Comment> findAllByEventIdIn(List<Long> eventIds);
}
