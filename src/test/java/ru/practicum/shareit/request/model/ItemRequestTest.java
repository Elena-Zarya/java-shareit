package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestTest {

    @InjectMocks
    private ItemRequest itemRequest;
    private User user;
    private LocalDateTime created;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "user@email.com");
        created = LocalDateTime.of(2023, 6, 7, 15, 0);
    }

    @Test
    void getId() {
        itemRequest.setId(1L);
        Long id = itemRequest.getId();
        assertEquals(1L, id);
    }

    @Test
    void getDescription() {
        itemRequest.setDescription("description");
        String description = itemRequest.getDescription();
        assertEquals("description", description);
    }

    @Test
    void getRequestor() {
        itemRequest.setRequestor(user);
        User requestor = itemRequest.getRequestor();
        assertEquals(user, requestor);
    }

    @Test
    void getCreated() {
        itemRequest.setCreated(created);
        LocalDateTime cretedSaved = itemRequest.getCreated();
        assertEquals(created, cretedSaved);
    }
}