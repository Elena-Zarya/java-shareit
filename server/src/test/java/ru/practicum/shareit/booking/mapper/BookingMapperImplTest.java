package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class BookingMapperImplTest {
    @InjectMocks
    private BookingMapperImpl bookingMapper;
    private UserDto userDto;
    private ItemDto itemDto;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void start() {
        start = LocalDateTime.of(2023, 8, 15, 15, 0);
        end = LocalDateTime.of(2023, 8, 16, 15, 0);
        userDto = new UserDto(1L, "user", "user@email.com");
        itemDto = new ItemDto(1L, "name", "description", true, userDto, null, null, null, null);
        bookingRequestDto = new BookingRequestDto(1L, start, end, 1L);
        User user = new User(1L, "user", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
    }

    @Test
    void dtoToBooking() {
        Booking bookingSaved = bookingMapper.dtoToBooking(bookingRequestDto);
        assertEquals(1L, bookingSaved.getId());
        assertEquals(start, bookingSaved.getStart());
        assertEquals(end, bookingSaved.getEnd());
    }

    @Test
    void dtoToBooking_whenBookingRequestDtoIsNull_thenReturnNull() {
        Booking bookingSaved = bookingMapper.dtoToBooking(null);
        assertNull(bookingSaved);
    }

    @Test
    void bookingToDto() {
        BookingResponseDto bookingResponseDtoSaved = bookingMapper.bookingToDto(booking);
        assertEquals(1L, bookingResponseDtoSaved.getId());
        assertEquals(start, bookingResponseDtoSaved.getStart());
        assertEquals(end, bookingResponseDtoSaved.getEnd());
        assertEquals(itemDto, bookingResponseDtoSaved.getItem());
        assertEquals(userDto, bookingResponseDtoSaved.getBooker());
        assertEquals(Status.WAITING, bookingResponseDtoSaved.getStatus());
    }

    @Test
    void bookingToDto_whenBookingIsNull_thenReturnNull() {
        BookingResponseDto bookingResponseDtoSaved = bookingMapper.bookingToDto(null);
        assertNull(bookingResponseDtoSaved);
    }

    @Test
    void bookingToDtoForItem() {
        BookingDtoForItem bookingDtoForItemDtoSaved = bookingMapper.bookingToDtoForItem(booking);
        assertEquals(1L, bookingDtoForItemDtoSaved.getId());
        assertEquals(start, bookingDtoForItemDtoSaved.getStart());
        assertEquals(end, bookingDtoForItemDtoSaved.getEnd());
        assertEquals(Status.WAITING, bookingDtoForItemDtoSaved.getStatus());
    }

    @Test
    void bookingToDtoForItem_whenBookingIsNull_shouldReturnNull() {
        BookingDtoForItem bookingDtoForItemDtoSaved = bookingMapper.bookingToDtoForItem(null);
        assertNull(bookingDtoForItemDtoSaved);
    }

    @Test
    void userToUserDto_whenUserIsNull_thenReturnNull() {
        UserDto userDtoSaved = bookingMapper.userToUserDto(null);
        assertNull(userDtoSaved);
    }

    @Test
    void itemToItemDto_whenItemIsNull_thenReturnNull() {
        ItemDto itemDtoSaved = bookingMapper.itemToItemDto(null);
        assertNull(itemDtoSaved);
    }
}
