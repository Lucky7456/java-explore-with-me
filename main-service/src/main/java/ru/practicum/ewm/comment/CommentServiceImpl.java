package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDto> findAllBy(List<Long> users, List<Long> events, LocalDateTime start, LocalDateTime end, int from, int size) {
        return commentRepository.findAllBy(
                users,
                events,
                start == null ? LocalDateTime.now().minusYears(1) : start,
                end == null ? LocalDateTime.now() : end,
                PageRequest.of(from, size)
        ).stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public List<CommentDto> findAllByEvent(long eventId, int from, int size) {
        eventRepository.findById(eventId).orElseThrow();
        return commentRepository.findAllByEventId(eventId, PageRequest.of(from, size)).stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public List<CommentDto> findAllRecent(int from, int size) {
        return commentRepository.findAll(PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public List<CommentDto> findAllByUser(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow();
        return commentRepository.findAllByAuthorId(userId, PageRequest.of(from, size)).stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public CommentDto findById(long userId, long commentId) {
        return CommentMapper.toCommentDto(commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow());
    }

    @Override
    @Transactional
    public CommentDto create(long userId, long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(author, event, newCommentDto)));
    }

    @Override
    @Transactional
    public CommentDto update(long userId, long commentId, NewCommentDto commentDto) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow();
        comment.setText(commentDto.text());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(long userId, long commentId) {
        commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow();
        commentRepository.deleteById(commentId);

    }

    @Override
    @Transactional
    public void delete(long commentId) {
        commentRepository.findById(commentId).orElseThrow();
        commentRepository.deleteById(commentId);
    }
}
