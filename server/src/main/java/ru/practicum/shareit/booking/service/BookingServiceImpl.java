package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.Pages;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingDtoRequest, Long userId) {
        Long itemId = bookingDtoRequest.getItemId();
        LocalDateTime start = bookingDtoRequest.getStart();
        LocalDateTime end = bookingDtoRequest.getEnd();
        if (itemId == null) {
            log.info("itemId is null");
            throw new NotFoundException("itemId is null");
        }
        if (start == null || end == null || end.isBefore(LocalDateTime.now()) || start.isBefore(LocalDateTime.now()) ||
                end.isBefore(start) || end.isEqual(start)) {
            log.info("booking date error");
            throw new ValidationException("booking date error");
        }
        ItemDto item = itemService.getItemById(itemId, userId);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            log.info("create from owner to item " + itemId);
            throw new NotFoundException("create from owner to item " + itemId);
        }
        if (!item.getAvailable()) {
            log.info("item " + itemId + " is not available");
            throw new ValidationException("item " + itemId + " is not available");
        }
        UserDto user = userService.getUserById(userId);
        Booking booking = bookingMapper.dtoToBooking(bookingDtoRequest);
        booking.setItem(itemMapper.dtoToItem(item));
        booking.setBooker(userMapper.dtoToUser(user));
        booking.setStatus(Status.WAITING);
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.bookingToDto(bookingSaved);
    }

    @Transactional
    @Override
    public BookingResponseDto updateStatus(Long bookingId, Long userId, boolean approved) {
        Booking booking = checkBooking(bookingId);
        if (Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                log.info("change status by user " + userId + " after approve");
                throw new StatusException("change status by user " + userId + " after approve");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            Booking bookingSaved = bookingRepository.save(booking);
            return bookingMapper.bookingToDto(bookingSaved);
        } else {
            log.info("no bookings found for the user " + userId);
            throw new NotFoundException("no bookings found for the user " + userId);
        }
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(booking.getItem().getOwner().getId(),
                userId)) {
            return bookingMapper.bookingToDto(booking);
        } else {
            log.info("no bookings found for the user " + userId);
            throw new NotFoundException("no bookings found for the user " + userId);
        }
    }

    @Override
    public Collection<BookingResponseDto> findAllBookingsByUser(Long userId, String state, int from, int size) {
        Pageable page = Pages.getPage(from, size);

        UserDto user = userService.getUserById(userId);
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllBookingByBookerIdOrderByStartDesc(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                bookings = bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllBookingByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllBookingByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllBookingByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
            default:
                log.info("Unknown state: UNSUPPORTED_STATUS");
                throw new StatusException(state);
        }
        return bookings.stream().map(bookingMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingResponseDto> findAllBookingsByOwner(Long ownerId, String state, int from, int size) {
        Pageable page = Pages.getPage(from, size);

        UserDto user = userService.getUserById(ownerId);
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllBookingByOwner(ownerId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllBookingByOwnerCurrent(ownerId, LocalDateTime.now(), page);
                break;
            case "PAST":
                bookings = bookingRepository.findAllBookingByOwnerPast(ownerId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllBookingByOwnerFuture(ownerId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllBookingByOwnerByStatus(ownerId, String.valueOf(Status.WAITING), page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllBookingByOwnerByStatus(ownerId, String.valueOf(Status.REJECTED), page);
                break;
            default:
                log.info("Unknown state: UNSUPPORTED_STATUS");
                throw new StatusException(state);
        }

        return bookings.stream().map(bookingMapper::bookingToDto).collect(Collectors.toList());
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            log.info("booking " + bookingId + " not found");
            throw new NotFoundException("booking " + bookingId + " not found");
        }
        return bookingOptional.get();
    }
}
