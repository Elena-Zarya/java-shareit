package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, List<Long>> itemsByOwner = new HashMap<>();

    @Override
    public Map<Long, User> findAll() {
        return users;
    }

    @Override
    public User save(User user, long userId) {
        users.put(userId, user);
        return user;
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

    @Override
    public void setItemsByOwner(long userId, List<Long> itemsList) {
        itemsByOwner.put(userId, itemsList);
    }

    @Override
    public List<Long> getItemsByOwner(long ownerId) {
        return itemsByOwner.get(ownerId);
    }
}
