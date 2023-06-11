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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

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
        User userSaved = userStorage.save(user);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        checkUserId(userId);
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
        User userSaved = userStorage.updateUser(user, userId);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto getUserById(long userId) {
        checkUserId(userId);
        User user = userStorage.getUserById(userId);
        return userMapper.userToDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        checkUserId(userId);
        userStorage.deleteUser(userId);
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
        checkUserId(userId);
        userStorage.getUserById(userId).getItemsList().add(itemId);
    }

    public boolean checkUserId(long userId) {
        if (userStorage.findAll().containsKey(userId)) {
            return true;
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
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
}
