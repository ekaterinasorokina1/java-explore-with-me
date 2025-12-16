package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.CommentStatus;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    private UserShortDto commentator;

    private Long event;

    private CommentStatus status;
}
