package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50, message = "Заголовок должен содержать от 1 до 50 символов.")
    private String title;
}
