package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;
    private final EntityManager em;
    private User user;
    private User user2;

    @BeforeEach
    void start() {
        user = new User(null, "name", "user@email.com");
        user2 = new User(null, "name2", "user2@email.com");
        em.persist(user);
        em.persist(user2);
    }

    @AfterEach
    void end() {
        em.remove(user2);
        em.remove(user);
    }

    @Test
    void getUserById() {
        long userId = user.getId();

        UserDto userDto = userService.getUserById(userId);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void getUserByIdShouldReturnNotFoundException() {
        long userId = 1000L;

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}