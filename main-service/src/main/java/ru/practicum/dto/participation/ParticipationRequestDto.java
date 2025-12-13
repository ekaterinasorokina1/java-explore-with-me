package ru.practicum.dto.participation;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private String status;
}
