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
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
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
        Long userId = user.getId();
        if (userId != null && checkEmail(email, userId)) {
            log.info("email " + email + " already exist");
            throw new EmailAlreadyExistException("email " + email + " already exist");
        }
        User userSaved = userRepository.save(user);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkUserId(userId);
        String nameNew = userDto.getName();
        String emailNew = userDto.getEmail();
        User user = userRepository.findById(userId).orElse(null);
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
        User userSaved = userRepository.save(user);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkUserId(userId);
        User user = userRepository.findById(userId).orElse(null);
        return userMapper.userToDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserId(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.userToDto(user));
        }
        return usersDto;
    }

    private boolean checkUserId(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return true;
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    private boolean checkEmail(String email, Long userId) {
        for (User user : userRepository.findAll()) {
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
