package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatedCommentDto {
    @NotBlank
    @Size(min = 3, max = 2000, message = "Комментарий должен содержать от 3 до 2000 символов.")
    private String text;
}
