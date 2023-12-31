package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;

public class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(comment.getId(),
                comment.getEvent().getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated());
    }
}
