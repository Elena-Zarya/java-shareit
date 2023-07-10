package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemTest {

    @InjectMocks
    private Item item;
    private User user;
    private ItemRequest request;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "user@email.com");
        LocalDateTime created = LocalDateTime.of(2023, 6, 7, 15, 0);
        request = new ItemRequest(1L, "description", user, created);
    }

    @Test
    void getId() {
        item.setId(1L);
        Long id = item.getId();
        assertEquals(1L, id);
    }

    @Test
    void getName() {
        item.setName("name");
        String name = item.getName();
        assertEquals("name", name);
    }

    @Test
    void getDescription() {
        item.setDescription("description");
        String description = item.getDescription();
        assertEquals("description", description);
    }

    @Test
    void getAvailable() {
        item.setAvailable(true);
        Boolean available = item.getAvailable();
        assertEquals(true, available);
    }

    @Test
    void getOwner() {
        item.setOwner(user);
        User owner = item.getOwner();
        assertEquals(user, owner);
    }

    @Test
    void getRequest() {
        item.setRequest(request);
        ItemRequest itemRequest = item.getRequest();
        assertEquals(request, itemRequest);
    }

    @Test
    void equals() {
        Item item1 = new Item();
        item1.setId(item.getId());
        item1.setRequest(item.getRequest());
        item1.setOwner(item.getOwner());
        item1.setAvailable(item.getAvailable());
        item1.setName(item.getName());
        item1.setDescription(item.getDescription());

        assertTrue(item.equals(item1));
    }

    @Test
    void testHashCode() {
        item.setId(1L);
        assertTrue(item.hashCode() > 0);
    }
}