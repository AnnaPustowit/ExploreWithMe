package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(Long userId, CommentRequestDto commentRequestDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getAllComments(Long eventId, String text,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            String sort, Integer from, Integer size);

    CommentResponseDto getCommentById(Long userId, Long commentId);

    CommentResponseDto deleteCommentById(Long userId, Long commentId);

    CommentResponseDto banComment(Long userId, Long commentId);

    CommentResponseDto publishComment(Long userId, Long commentId);

    List<CommentResponseDto> getCommentsByAdmin(Long userId, Long eventId, String text,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                String sort, Integer from, Integer size);
}
