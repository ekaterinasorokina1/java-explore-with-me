package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Описание должно содержать от 2 до 250 символов.")
    public String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "Описание должно содержать от 2 до 250 символов.")
    private String name;
}
