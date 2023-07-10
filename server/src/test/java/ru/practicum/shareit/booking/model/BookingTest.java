package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingTest {
    @InjectMocks
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private Item item;
    private Status status;

    @BeforeEach
    void start() {
        booker = new User(1L, "name", "user@email.com");
        User owner = new User(2L, "name", "user@email.com");
        start = LocalDateTime.of(2023, 6, 7, 15, 0);
        end = LocalDateTime.of(2023, 6, 10, 15, 0);
        LocalDateTime created = LocalDateTime.of(2023, 6, 7, 15, 0);
        status = Status.APPROVED;
        ItemRequest request = new ItemRequest(1L, "description", booker, created);
        item = new Item(1L, "name", "description", true, owner, request);
    }

    @Test
    void getId() {
        booking.setId(1L);
        long id = booking.getId();
        assertEquals(1L, id);
    }

    @Test
    void getStart() {
        booking.setStart(start);
        LocalDateTime startSaved = booking.getStart();
        assertEquals(start, startSaved);
    }

    @Test
    void getEnd() {
        booking.setEnd(end);
        LocalDateTime endSaved = booking.getEnd();
        assertEquals(end, endSaved);
    }

    @Test
    void getItem() {
        booking.setItem(item);
        Item itemSaved = booking.getItem();
        assertEquals(item, itemSaved);
    }

    @Test
    void getBooker() {
        booking.setBooker(booker);
        User bookerSaved = booking.getBooker();
        assertEquals(booker, bookerSaved);
    }

    @Test
    void getStatus() {
        booking.setStatus(status);
        Status statusSaved = booking.getStatus();
        assertEquals(status, statusSaved);
    }

    @Test
    void testEquals() {
        Booking booking1 = new Booking();
        booking1.setId(booking.getId());
        booking1.setStatus(booking.getStatus());
        booking1.setStart(booking.getStart());
        booking1.setEnd(booking.getEnd());
        booking1.setBooker(booking.getBooker());

        assertTrue(booking.equals(booking1));
    }

    @Test
    void testHashCode() {
        booking.setId(1L);
        assertTrue(booking.hashCode() > 0);
    }
}