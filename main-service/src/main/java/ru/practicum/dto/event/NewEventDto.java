package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Аннотация должна содержать от 20 до 2000 символов.")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Описание должно содержать от 20 до 7000 символов.")
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit = 0;

    private boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120, message = "Заголовок должен содержать от 3 до 120 символов.")
    private String title;
}
