package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    private final EntityManager em;
    private ItemRequest itemRequest;
    private User user;
    private Item item;

    @BeforeEach
    void start() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
        user = new User(null, "name", "user@email.com");
        item = new Item(null, "name", "description", true, user, null);
        itemRequest = new ItemRequest(null, "description", user, created);
        em.persist(user);
        em.persist(item);
        em.persist(itemRequest);
    }

    @AfterEach
    void end() {
        em.remove(itemRequest);
        em.remove(item);
        em.remove(user);
    }

    @Test
    void findAllItemRequestByUser() {
        long userId = user.getId();

        Collection<ItemRequestDto> itemRequestDtoCollection = itemRequestService.findAllItemRequestByUser(userId);
        assertEquals(1, itemRequestDtoCollection.size());
    }

    @Test
    void findAllItemRequestByUserShouldReturnNotFoundException() {
        long userId = 1000L;

        assertThrows(NotFoundException.class, () -> itemRequestService.findAllItemRequestByUser(userId));
    }
}