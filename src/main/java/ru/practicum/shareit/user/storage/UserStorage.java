package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> findAll();

    User save(User user, long userId);

    User findUserById(long userId);

    User getUserById(long userId);

    boolean deleteUser(long userId);

    void setItemsByOwner(long userId, List<Long> itemsList);

    List<Long> getItemsByOwner(long ownerId);
}
