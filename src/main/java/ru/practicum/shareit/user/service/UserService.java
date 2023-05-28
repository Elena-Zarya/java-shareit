package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long userId);

    void deleteUser(long userId);

    Collection<UserDto> getAllUsers();

    void addItem(long itemId, long userId);

    List<Long> getItemsByOwner(long ownerId);
}
