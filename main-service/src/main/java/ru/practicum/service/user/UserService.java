package ru.practicum.service.user;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getAllUsers(List<Long> ids, Pageable pageable);

    public UserDto createUser(NewUserRequest newUserRequest);

    public void deleteUserById(Long userId);
}
