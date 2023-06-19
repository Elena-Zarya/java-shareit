package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingDtoRequest, Long userId);

    BookingResponseDto updateStatus(Long bookingId, Long userId, boolean approved);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    Collection<BookingResponseDto> findAllBookingsByUser(Long userId, String status);

    Collection<BookingResponseDto> findAllBookingsByOwner(Long userId, String state);
}
