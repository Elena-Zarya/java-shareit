package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received POST request: new user");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("userId") long userId) {
        log.info("Received PATCH request: update user id {}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        log.info("Received GET request: get user by id {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("Received DELETE request: delete user {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAllUsers();
    }
}
