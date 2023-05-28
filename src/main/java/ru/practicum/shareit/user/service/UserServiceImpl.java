package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private long id;

    @Override
    public UserDto addUser(UserDto userDto) {
        String email = userDto.getEmail();
        String name = userDto.getName();
        if (email == null) {
            throw new ValidationException("email is empty");
        }
        if (name == null) {
            throw new ValidationException("name is empty");
        }
        User user = userMapper.dtoToUser(userDto);
        long userId = user.getId();
        if (checkEmail(email, userId)) {
            log.info("email " + email + " already exist");
            throw new EmailAlreadyExistException("email " + email + " already exist");
        }
        if (userId == 0) {
            userId = generateId();
            user.setId(userId);
        }
        User userSaved = userStorage.save(user, userId);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        if (checkUserId(userId)) {
            String nameNew = userDto.getName();
            String emailNew = userDto.getEmail();
            User user = userStorage.findUserById(userId);
            if (emailNew != null) {
                if (checkEmail(emailNew, userId)) {
                    log.info("email " + emailNew + " already exist");
                    throw new EmailAlreadyExistException("email " + emailNew + " already exist");
                }
                user.setEmail(emailNew);
            }
            if (nameNew != null) {
                user.setName(nameNew);
            }
            User userSaved = userStorage.save(user, userId);
            return userMapper.userToDto(userSaved);
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    @Override
    public UserDto getUserById(long userId) {
        if (checkUserId(userId)) {
            User user = userStorage.getUserById(userId);
            return userMapper.userToDto(user);
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    @Override
    public void deleteUser(long userId) {
        if (checkUserId(userId)) {
            userStorage.deleteUser(userId);
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userStorage.findAll().values();
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.userToDto(user));
        }
        return usersDto;
    }

    @Override
    public void addItem(long itemId, long userId) {
        if (checkUserId(userId)) {
            userStorage.getUserById(userId).getItemsList().add(itemId);
            userStorage.setItemsByOwner(userId, userStorage.getUserById(userId).getItemsList());
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    @Override
    public List<Long> getItemsByOwner(long ownerId) {
        return userStorage.getItemsByOwner(ownerId);
    }

    private boolean checkEmail(String email, long userId) {
        for (User user : userStorage.findAll().values()) {
            if (user.getEmail().equals(email)) {
                if (user.getId() == userId) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkUserId(long userId) {
        return userStorage.findAll().containsKey(userId);
    }

    private long generateId() {
        return ++id;
    }
}
