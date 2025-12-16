package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.model.CommentStatus;

@Data
public class UpdateCommentStatusDto {
    @NotNull
    private CommentStatus status;
}
