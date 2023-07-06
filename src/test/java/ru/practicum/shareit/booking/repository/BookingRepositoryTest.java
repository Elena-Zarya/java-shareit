package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Sql({"/schema.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllBookingByOwnerShouldReturnBookingId1AndId2() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwner(itemSaved.getOwner().getId(), page);

        assertEquals(2, bookingList.size());
        assertEquals(booking2Saved.getId(), bookingList.get(0).getId());
        assertEquals(bookingSaved.getId(), bookingList.get(1).getId());
        assertEquals(start, bookingList.get(1).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(1).getItem());
        assertEquals(Status.WAITING, bookingList.get(1).getStatus());
    }

    @Test
    void findAllBookingByOwnerShouldReturnBookingId1() {
        int from = 1;
        int size = 1;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwner(itemSaved.getOwner().getId(), page);

        assertEquals(1, bookingList.size());
        assertEquals(bookingSaved.getId(), bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(0).getItem());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findAllBookingByOwnerShouldReturnListIsEmpty() {
        int from = 1;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwner(user2Saved.getId(), page);

        assertEquals(0, bookingList.size());
    }

    @Test
    void findAllBookingByOwnerCurrentShouldReturnBookingId2() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 16, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerCurrent(itemSaved.getOwner().getId(), date, page);

        assertEquals(1, bookingList.size());
        assertEquals(booking2Saved.getId(), bookingList.get(0).getId());
        assertEquals(start2, bookingList.get(0).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(0).getItem());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findAllBookingByOwnerCurrentShouldReturnListIsEmpty() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 17, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerCurrent(itemSaved.getOwner().getId(), date, page);

        assertEquals(0, bookingList.size());
    }

    @Test
    void findAllBookingByOwnerPastShouldReturnBookingId1AndId2() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 18, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerPast(itemSaved.getOwner().getId(), date, page);

        assertEquals(2, bookingList.size());
        assertEquals(booking2Saved.getId(), bookingList.get(0).getId());
        assertEquals(start2, bookingList.get(0).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(0).getItem());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findAllBookingByOwnerPastShouldReturnListIsEmpty() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 10, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerPast(itemSaved.getOwner().getId(), date, page);

        assertEquals(0, bookingList.size());
    }

    @Test
    void findAllBookingByOwnerFutureShouldReturnBookingId2() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 15, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerFuture(itemSaved.getOwner().getId(), date, page);

        assertEquals(1, bookingList.size());
        assertEquals(booking2Saved.getId(), bookingList.get(0).getId());
        assertEquals(start2, bookingList.get(0).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(0).getItem());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findAllBookingByOwnerFutureShouldReturnListIsEmpty() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        LocalDateTime date = LocalDateTime.of(2023, 8, 15, 17, 0);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerFuture(user2Saved.getId(), date, page);

        assertEquals(0, bookingList.size());
    }

    @Test
    void findAllBookingByOwnerByStatusShouldReturnBookingId1AndId2() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        String status = String.valueOf(Status.WAITING);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerByStatus(itemSaved.getOwner().getId(), status, page);

        assertEquals(2, bookingList.size());
        assertEquals(booking2Saved.getId(), bookingList.get(0).getId());
        assertEquals(bookingSaved.getId(), bookingList.get(1).getId());
        assertEquals(start, bookingList.get(1).getStart());
        assertEquals(user2Saved, bookingList.get(0).getBooker());
        assertEquals(itemSaved, bookingList.get(1).getItem());
        assertEquals(Status.WAITING, bookingList.get(1).getStatus());
    }

    @Test
    void findAllBookingByOwnerByStatusShouldReturnListIsEmpty() {
        int from = 0;
        int size = 2;
        LocalDateTime start = LocalDateTime.of(2023, 8, 15, 15, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 16, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 16, 15, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 17, 15, 0);

        User user = new User(1L, "name", "user@email.com");
        User userSaved = userRepository.save(user);

        User user2 = new User(2L, "name", "user2@email.com");
        User user2Saved = userRepository.save(user2);

        Item item = new Item(1L, "name", "description", true, userSaved, null);
        Item itemSaved = itemRepository.save(item);

        Booking booking = new Booking(1L, start, end, itemSaved, user2Saved, Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);

        Booking booking2 = new Booking(2L, start2, end2, itemSaved, user2Saved, Status.WAITING);
        Booking booking2Saved = bookingRepository.save(booking2);

        String status = String.valueOf(Status.REJECTED);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerByStatus(itemSaved.getOwner().getId(), status, page);

        assertEquals(0, bookingList.size());
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
    }
}