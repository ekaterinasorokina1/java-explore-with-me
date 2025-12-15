package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов.")
    private String name;
}
