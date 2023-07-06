package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @InjectMocks
    private User user;

    @Test
    void getId() {
        user.setId(1L);
        Long id = user.getId();
        assertEquals(1L, id);
    }

    @Test
    void getName() {
        user.setName("name");
        String name = user.getName();
        assertEquals("name", name);
    }

    @Test
    void getEmail() {
        user.setEmail("user@email.com");
        String email = user.getEmail();
        assertEquals("user@email.com", email);
    }
}