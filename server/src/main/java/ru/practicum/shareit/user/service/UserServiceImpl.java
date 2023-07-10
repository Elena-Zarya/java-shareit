package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        User userSaved = userRepository.save(user);
        return userMapper.userToDto(userSaved);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkUserId(userId);
        String nameNew = userDto.getName();
        String emailNew = userDto.getEmail();
        User user = userRepository.findById(userId).orElse(null);
        if (emailNew != null && !emailNew.isEmpty()) {
            if (checkEmail(emailNew, userId)) {
                log.info("email " + emailNew + " already exist");
                throw new EmailAlreadyExistException("email " + emailNew + " already exist");
            }
            user.setEmail(emailNew);
        }
        if (nameNew != null && !nameNew.isEmpty()) {
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

    @Transactional
    @Override
    public UserDto deleteUser(Long userId) {
        User user = checkUserId(userId);
        userRepository.deleteById(userId);
        return userMapper.userToDto(user);
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

    private User checkUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            log.info("User " + userId + " not found");
            throw new NotFoundException("User " + userId + " not found");
        }
    }

    private boolean checkEmail(String email, Long userId) {
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                if (Objects.equals(user.getId(), userId)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
}
