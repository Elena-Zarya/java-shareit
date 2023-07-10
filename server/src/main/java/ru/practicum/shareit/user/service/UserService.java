package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long userId);

    UserDto deleteUser(Long userId);

    Collection<UserDto> getAllUsers();
}
