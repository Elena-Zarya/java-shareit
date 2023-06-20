package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingRequestDto bookingDtoRequest,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received POST request: new booking");
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatus(@Valid @RequestParam("approved") boolean approved,
                                           @PathVariable("bookingId") Long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received PATCH request: update booking status id {}", bookingId);
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable("bookingId") Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET request: get booking by id {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponseDto> findAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Received GET request: get all booking by user {}", userId);
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> findAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Received GET request: get all booking by owner {}", ownerId);
        return bookingService.findAllBookingsByOwner(ownerId, state);
    }
}
