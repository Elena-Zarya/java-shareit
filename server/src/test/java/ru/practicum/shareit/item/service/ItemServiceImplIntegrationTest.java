package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;
    private final EntityManager em;
    private User user;
    private Item item;
    private Item item2;

    @BeforeEach
    void start() {
        user = new User(null, "user", "user@email.com");
        item = new Item(null, "name", "description", true, user, null);
        item2 = new Item(null, "name2", "description2", true, user, null);
        em.persist(user);
        em.persist(item);
        em.persist(item2);
    }

    @AfterEach
    void end() {
        em.remove(item);
        em.remove(item2);
        em.remove(user);
    }

    @Test
    void getAllItemByUser() {
        int from = 0;
        int size = 5;
        long ownerId = item.getOwner().getId();

        Collection<ItemDto> itemsByUsers = itemService.getAllItemByUser(ownerId, from, size);
        assertEquals(2, itemsByUsers.size());
    }

    @Test
    void getAllItemByUserShouldReturnNotFoundException() {
        int from = 0;
        int size = 5;
        long ownerId = 1000L;

        assertThrows(NotFoundException.class, () -> itemService.getAllItemByUser(ownerId, from, size));
    }
}