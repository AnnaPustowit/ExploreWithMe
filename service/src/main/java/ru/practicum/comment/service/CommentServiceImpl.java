package ru.practicum.comment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentState;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDto createComment(Long userId, CommentRequestDto commentRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        validateUser(user, userId);
        Optional<Event> event = eventRepository.findById(commentRequestDto.getEventId());
        if (event.isEmpty()) {
            throw new InvalidParameterException("Нет события с id:" + commentRequestDto.getEventId());
        }
        if (!event.get().getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие еще не опубликовано");
        }

        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setUser(user.get());
        comment.setEvent(event.get());
        comment.setCreated(LocalDateTime.now());
        comment.setCommentState(CommentState.PENDING);

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        validateUser(user, userId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        validateComment(comment, commentId);
        comment.get().setText(commentRequestDto.getText());
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment.get()));
    }

    @Override
    public List<CommentResponseDto> getAllComments(Long eventId, String text,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   String sort, Integer from, Integer size) {
        if (sort != null && !"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            throw new InvalidParameterException("Некорректные данные сортировки");
        }
        PageRequest pageable = PageRequest.of(from / size, size);
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new InvalidParameterException("Время начала должно быть раньше времени конца");
            }
        }
        return commentRepository.getComments(eventId, text, rangeStart, rangeEnd, sort, CommentState.PUBLISHED, pageable).stream()
                .map(c -> CommentMapper.toCommentResponseDto(c)).collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getCommentById(Long userId, Long commentId) {
        Optional<User> user = userRepository.findById(userId);
        validateUser(user, userId);
        return CommentMapper.toCommentResponseDto(commentRepository.getById(commentId));
    }

    @Override
    public CommentResponseDto deleteCommentById(Long userId, Long commentId) {
        Optional<User> user = userRepository.findById(userId);
        validateUser(user, userId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        validateComment(comment, commentId);
        if (!comment.get().getCommentState().equals(CommentState.PENDING)) {
            throw new ConflictException("Удалить комментарий можно только в состоянии PENDING");
        }
        commentRepository.deleteById(commentId);
        return CommentMapper.toCommentResponseDto(comment.get());
    }

    @Override
    public CommentResponseDto banComment(Long userId, Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        validateComment(comment, commentId);
        if (comment.get().getCommentState().equals(CommentState.CANCELED) || comment.get().getCommentState().equals(CommentState.PUBLISHED)) {
            throw new ConflictException("Отклонить комментарий можно только в состоянии PENDING");
        }

        comment.get().setCommentState(CommentState.CANCELED);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment.get()));
    }

    @Override
    public CommentResponseDto publishComment(Long userId, Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        validateComment(comment, commentId);
        if (comment.get().getCommentState().equals(CommentState.CANCELED) || comment.get().getCommentState().equals(CommentState.PUBLISHED)) {
            throw new ConflictException("Опубликовать комментарий можно только в состоянии PENDING");
        }

        comment.get().setCommentState(CommentState.PUBLISHED);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment.get()));
    }

    @Override
    public List<CommentResponseDto> getCommentsByAdmin(Long userId, Long eventId, String text,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       String sort, Integer from, Integer size) {
        if (sort != null && !"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            throw new InvalidParameterException("Некорректные данные сортировки");
        }
        PageRequest pageable = PageRequest.of(from / size, size);

        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new InvalidParameterException("Время начала должно быть раньше времени конца");
            }
        }

        return commentRepository.getCommentsByAdmin(userId, eventId, text, rangeStart, rangeEnd, sort, CommentState.PUBLISHED, pageable).stream()
                .map(c -> CommentMapper.toCommentResponseDto(c)).collect(Collectors.toList());
    }

    void validateUser(Optional<User> user, Long userId) {
        if (user.isEmpty()) {
            throw new InvalidParameterException("Нет пользователя с id:" + userId);
        }
    }

    void validateComment(Optional<Comment> comment, Long commentId) {
        if (comment.isEmpty()) {
            throw new EntityNotFoundException("Нет комментария с id:" + commentId);
        }
    }
}
