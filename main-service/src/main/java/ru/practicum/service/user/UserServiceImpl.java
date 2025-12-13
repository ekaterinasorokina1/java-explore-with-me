package ru.practicum.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return StreamSupport.stream(userRepository.findAll(pageable).spliterator(), false)
                    .map(UserMapper::toUserDto).toList();
        }
        return userRepository.findAllByIdIn(ids, pageable).stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        Optional<User> userExists = userRepository.findByEmail(newUserRequest.getEmail());

        if (userExists.isPresent()) {
            log.error("Данный email уже используется");
            throw new ConflictException("Данный email занят");
        }
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        getUser(userId);
        userRepository.deleteById(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
