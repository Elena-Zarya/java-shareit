package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.excrption.StatusException;
import ru.practicum.shareit.excrption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;

    private UserDto userDto;
    private ItemDto itemDto;
    private BookingResponseDto bookingResponseDto;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private User user;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void start() {
        start = LocalDateTime.of(2023, 8, 15, 15, 0);
        end = LocalDateTime.of(2023, 8, 16, 15, 0);
        userDto = new UserDto(1L, "user", "user@email.com");
        itemDto = new ItemDto(1L, "картина", "картина Весна", true, userDto, null, null, null, null);
        bookingResponseDto = new BookingResponseDto(1L, start, end, itemDto, userDto, Status.WAITING);
        bookingRequestDto = new BookingRequestDto(1L, start, end, 1L);
        user = new User(1L, "name", "user@email.com");
        item = new Item(1L, "name", "description", true, user, null);
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
    }

    @Test
    void createBooking_whenIsOk_thenReturnBookingResponseDto() {
        long userId = 2L;

        Mockito.when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingMapper.dtoToBooking(any())).thenReturn(booking);
        Mockito.when(itemMapper.dtoToItem(any())).thenReturn(item);
        Mockito.when(userMapper.dtoToUser(any())).thenReturn(user);
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto bookingResponseDtoSaved = bookingService.createBooking(bookingRequestDto, userId);
        assertEquals(1L, bookingResponseDtoSaved.getId());
        assertEquals(start, bookingResponseDtoSaved.getStart());
        assertEquals(end, bookingResponseDtoSaved.getEnd());
        assertEquals(itemDto, bookingResponseDtoSaved.getItem());
        assertEquals(userDto, bookingResponseDtoSaved.getBooker());
        assertEquals(Status.WAITING, bookingResponseDtoSaved.getStatus());

        verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void createBooking_whenOwnerIsBooking_thenReturnNotFoundException() {
        long userId = 1L;

        Mockito.when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, userId));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void createBooking_whenItemIdIsNull_thenReturnNotFoundException() {
        long userId = 2L;
        bookingRequestDto.setItemId(null);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, userId));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void createBooking_whenStartIsBefore_thenReturnValidationException() {
        long userId = 2L;
        bookingRequestDto.setStart(LocalDateTime.of(2023, 2, 12, 15, 0));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequestDto, userId));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void createBooking_whenAvailableIsFalse_thenReturnValidationException() {
        long userId = 2L;
        itemDto.setAvailable(false);

        Mockito.when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequestDto, userId));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void updateStatus_whenApprovedIsFalse_thenReturnBookingResponseDto() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = false;
        bookingResponseDto.setStatus(Status.REJECTED);

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto bookingResponseDtoSaved = bookingService.updateStatus(bookingId, userId, approved);
        assertEquals(1L, bookingResponseDtoSaved.getId());
        assertEquals(start, bookingResponseDtoSaved.getStart());
        assertEquals(end, bookingResponseDtoSaved.getEnd());
        assertEquals(itemDto, bookingResponseDtoSaved.getItem());
        assertEquals(userDto, bookingResponseDtoSaved.getBooker());
        assertEquals(Status.REJECTED, bookingResponseDtoSaved.getStatus());

        verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void updateStatus_whenApprovedIsTrue_thenReturnBookingResponseDto() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        bookingResponseDto.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto bookingResponseDtoSaved = bookingService.updateStatus(bookingId, userId, approved);
        assertEquals(1L, bookingResponseDtoSaved.getId());
        assertEquals(start, bookingResponseDtoSaved.getStart());
        assertEquals(end, bookingResponseDtoSaved.getEnd());
        assertEquals(itemDto, bookingResponseDtoSaved.getItem());
        assertEquals(userDto, bookingResponseDtoSaved.getBooker());
        assertEquals(Status.APPROVED, bookingResponseDtoSaved.getStatus());

        verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void updateStatus_whenUpdateAfterApproved_thenReturnStatusException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setStatus(Status.APPROVED);
        bookingResponseDto.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(StatusException.class, () -> bookingService.updateStatus(bookingId, userId, approved));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void updateStatus_whenUserNotFound_thenReturnNotFoundException() {
        long userId = 2L;
        long bookingId = 1L;
        boolean approved = true;

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(bookingId, userId, approved));
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void getBookingById_whenIsOk_thenReturnBookingResponseDto() {
        long userId = 1L;
        long bookingId = 1L;

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto bookingResponseDtoSaved = bookingService.getBookingById(bookingId, userId);
        assertEquals(1L, bookingResponseDtoSaved.getId());
        assertEquals(start, bookingResponseDtoSaved.getStart());
        assertEquals(end, bookingResponseDtoSaved.getEnd());
        assertEquals(itemDto, bookingResponseDtoSaved.getItem());
        assertEquals(userDto, bookingResponseDtoSaved.getBooker());
        assertEquals(Status.WAITING, bookingResponseDtoSaved.getStatus());
    }

    @Test
    void getBookingById_whenBookingNotFound_thenReturnNotFoundException() {
        long userId = 1L;
        long bookingId = 1000L;

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getBookingById_whenUserIsNotOwnerOrBooker_thenReturnNotFoundException() {
        long userId = 6L;
        long bookingId = 1L;

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void findAllBookingsByUser_whenStatusIsAll_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "ALL";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId,
                status, from, size);

        assertEquals(1, allBookingsByUsers.size());
    }

    @Test
    void findAllBookingsByUser_whenStatusIsCurrent_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "CURRENT";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId, status, from,
                size);

        assertEquals(1, allBookingsByUsers.size());
    }

    @Test
    void findAllBookingsByUser_whenStatusIsPast_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "PAST";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId, status,
                from, size);

        assertEquals(1, allBookingsByUsers.size());
    }

    @Test
    void findAllBookingsByUser_whenStatusIsFuture_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "FUTURE";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId, status,
                from, size);

        assertEquals(1, allBookingsByUsers.size());
    }

    @Test
    void findAllBookingsByUser_whenStatusIsWaiting_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "WAITING";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(Status.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId, status,
                from, size);

        assertEquals(1, allBookingsByUsers.size());
    }

    @Test
    void findAllBookingsByUser_whenStatusIsRejected_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "REJECTED";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(Status.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByUsers = bookingService.findAllBookingsByUser(userId, status,
                from, size);

        assertEquals(1, allBookingsByUsers.size());
    }


    @Test
    void findAllBookingsByUser_whenStatusIsUnsupportedStatus_thenReturnBookingDtoCollection() {
        long userId = 1L;
        String status = "UNSUPPORTED_STATUS";
        int from = 0;
        int size = 1;

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);

        assertThrows(StatusException.class, () -> bookingService.findAllBookingsByUser(userId, status,
                from, size));
    }

    @Test
    void findAllBookingsByOwner_whenStatusIsAll_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "ALL";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwner(anyLong(), any()))
                .thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId,
                status, from, size);

        assertEquals(1, allBookingsByOwner.size());
    }

    @Test
    void findAllBookingsByOwner_whenStatusIsCurrent_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "CURRENT";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwnerCurrent(anyLong(), any(LocalDateTime.class),
                any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, status, from,
                size);

        assertEquals(1, allBookingsByOwner.size());
    }

    @Test
    void allBookingsByOwner_whenStatusIsPast_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "PAST";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwnerPast(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, status,
                from, size);

        assertEquals(1, allBookingsByOwner.size());
    }

    @Test
    void findAllBookingsByOwner_whenStatusIsFuture_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "FUTURE";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwnerFuture(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, status,
                from, size);

        assertEquals(1, allBookingsByOwner.size());
    }

    @Test
    void findAllBookingsByOwner_whenStatusIsWaiting_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "WAITING";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwnerByStatus(anyLong(),
                anyString(), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, status,
                from, size);

        assertEquals(1, allBookingsByOwner.size());
    }

    @Test
    void findAllBookingsByOwner_whenStatusIsRejected_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "REJECTED";
        int from = 0;
        int size = 1;
        List<Booking> bookingList = List.of(booking);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(bookingRepository.findAllBookingByOwnerByStatus(anyLong(),
                anyString(), any())).thenReturn(bookingList);
        Mockito.when(bookingMapper.bookingToDto(any())).thenReturn(bookingResponseDto);

        Collection<BookingResponseDto> allBookingsByOwner = bookingService.findAllBookingsByOwner(ownerId, status,
                from, size);

        assertEquals(1, allBookingsByOwner.size());
    }


    @Test
    void findAllBookingsByOwner_whenStatusIsUnsupportedStatus_thenReturnBookingDtoCollection() {
        long ownerId = 1L;
        String status = "UNSUPPORTED_STATUS";
        int from = 0;
        int size = 1;

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);

        assertThrows(StatusException.class, () -> bookingService.findAllBookingsByOwner(ownerId, status,
                from, size));
    }
}