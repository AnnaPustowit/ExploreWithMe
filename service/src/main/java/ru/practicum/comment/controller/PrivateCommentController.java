package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable @Positive Long userId, @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Создание комментария");
        return commentService.createComment(userId, commentRequestDto);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId, @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Редактирование комментария");
        return commentService.updateComment(userId, commentId, commentRequestDto);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        log.info("Получить комментарий с userId: {}, commentId: {}", userId, commentId);
        return commentService.getCommentById(userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public CommentResponseDto deleteComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        log.info("Удалить комментарий по его id: {}", commentId);
        return commentService.deleteCommentById(userId, commentId);
    }
}
