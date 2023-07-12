package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    Long id;
    Long eventId;
    String text;
    String authorName;
    LocalDateTime created;
}
