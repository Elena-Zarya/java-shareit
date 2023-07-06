package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
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
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    private final EntityManager em;
    private Booking booking;
    private User user;
    private User user2;
    private Item item;

    @BeforeEach
    void start() {
        user = new User(null, "name", "user@email.com");
        user2 = new User(null, "name2", "user2@email.com");
        item = new Item(null, "name", "description", true, user, null);
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        booking = new Booking(null, start, end, item, user2, Status.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
    }

    @AfterEach
    void end() {
        em.remove(booking);
        em.remove(item);
        em.remove(user);
        em.remove(user2);
    }

    @Test
    void findAllBookingsByOwner() {
        int from = 0;
        int size = 5;
        String state = "WAITING";
        long ownerId = user.getId();

        Collection<BookingResponseDto> bookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, state, from, size);
        assertEquals(1, bookingsByOwner.size());
    }

    @Test
    void findAllBookingsByOwnerShouldReturnNotFoundException() {
        int from = 0;
        int size = 5;
        String state = "WAITING";
        long ownerId = 1000L;

        assertThrows(NotFoundException.class, () -> bookingService.findAllBookingsByOwner(ownerId, state, from, size));
    }
}