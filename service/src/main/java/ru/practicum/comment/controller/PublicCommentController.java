package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseDto> getAllComments(@PathVariable @Positive Long eventId,
                                                   @RequestParam(required = false) String text,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                   @RequestParam(required = false) String sort, @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получить комментарии события по его id: {}", eventId);
        return commentService.getAllComments(eventId, text, rangeStart, rangeEnd, sort, from, size);
    }
}
