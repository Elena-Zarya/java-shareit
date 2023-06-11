package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public Map<Long, User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        generateId();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User updateUser(User user, long userId) {
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public User findUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public boolean deleteUser(long userId) {
        users.remove(userId);
        if (!users.containsKey(userId)) {
            log.info("Deleted user id: {}", userId);
            return true;
        } else {
            log.info("User id: {} not deleted", userId);
            return false;
        }
    }

    private long generateId() {
        return ++id;
    }
}
