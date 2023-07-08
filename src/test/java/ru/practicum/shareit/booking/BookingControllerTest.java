package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto bookingResponseDto2;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void start() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 15, 15, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 16, 15, 0);
        UserDto userDto = new UserDto(1L, "user", "user@email.com");
        ItemDto itemDto = new ItemDto(1L, "картина", "картина Весна", true, userDto, null, null, null, null);
        bookingResponseDto = new BookingResponseDto(1L, start, end, itemDto, userDto, Status.WAITING);
        bookingResponseDto2 = new BookingResponseDto(2L, start, end, itemDto, userDto, Status.WAITING);
        bookingRequestDto = new BookingRequestDto(1L, start, end, 1L);
    }

    @Test
    void createBooking_shouldReturnBookingResponseDto() throws Exception {
        long userId = 1L;
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(bookingJsonResponse()));
    }

    @Test
    void createBooking_whenUserNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1000L;
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(bookingJsonResponse()));
    }

    @Test
    void updateStatus_whenBookingNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1L;
        long bookingId = 1000L;
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_whenIncorrectStatus_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "status")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(bookingJsonResponse()));
    }

    @Test
    void getBookingById_whenBookingNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1L;
        long bookingId = 1000L;
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookingsByUser() throws Exception {
        long userId = 1L;
        List<BookingResponseDto> bookingResponseDtoList = List.of(bookingResponseDto, bookingResponseDto2);
        when(bookingService.findAllBookingsByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingResponseDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingResponseDtoList.get(1).getId()), Long.class));
    }

    @Test
    void findAllBookingsByUser_whenUserNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1000L;
        when(bookingService.findAllBookingsByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookingsByOwner() throws Exception {
        long ownerId = 1L;
        List<BookingResponseDto> bookingResponseDtoList = List.of(bookingResponseDto, bookingResponseDto2);
        when(bookingService.findAllBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingResponseDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingResponseDtoList.get(1).getId()), Long.class));
    }

    @Test
    void findAllBookingsByOwner_whenOwnerNotFound_shouldReturnNotFoundException() throws Exception {
        long ownerId = 1000L;
        when(bookingService.findAllBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private String bookingJsonResponse() {
        return "{" +
                "\"id\":1," +
                "\"start\":\"2023-06-15T15:00:00\"," +
                "\"end\":\"2023-06-16T15:00:00\"," +
                "\"item\":{\"id\":1," +
                "\"name\":\"картина\"," +
                "\"description\":\"картина Весна\"," +
                "\"available\":true," +
                "\"owner\":{" +
                "\"id\":1," +
                "\"name\":\"user\"," +
                "\"email\":\"user@email.com\"}," +
                "\"lastBooking\":null," +
                "\"nextBooking\":null," +
                "\"comments\":null," +
                "\"requestId\":null" +
                "}," +
                "\"booker\":{\"id\":1," +
                "\"name\":\"user\"," +
                "\"email\":\"user@email.com\"" +
                "}," +
                "\"status\":\"WAITING\"" +
                "}";
    }
}