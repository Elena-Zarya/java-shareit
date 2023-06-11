package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Long, User> findAll();

    User save(User user);

    User updateUser(User user, long userId);

    User findUserById(long userId);

    User getUserById(long userId);

    boolean deleteUser(long userId);
}
