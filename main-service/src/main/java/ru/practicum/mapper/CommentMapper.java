package ru.practicum.mapper;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdatedCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toEntityComment(NewCommentDto dto, Event event, User commentator) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCommentator(commentator);
        comment.setEvent(event);
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setUpdated(comment.getUpdated());
        dto.setCommentator(UserMapper.toUserShortDto(comment.getCommentator()));
        dto.setEvent(comment.getId());
        dto.setStatus(comment.getStatus());
        return dto;
    }

    public static Comment commentFromUpdate(Comment comment, UpdatedCommentDto dto) {
        if (!dto.getText().isEmpty()) {
            comment.setText(dto.getText());
        }
        comment.setUpdated(LocalDateTime.now());
        return comment;
    }
}
