package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto2;
    List<ItemDto> items;

    @BeforeEach
    void start() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
        UserDto userDto = new UserDto(1L, "user", "user@email.com");
        items = List.of(new ItemDto(1L, "name", "description", true, userDto,
                null, null, null, 1L), new ItemDto(2L, "name2",
                "description2", true, userDto, null, null, null,
                1L));
        itemRequestDto = new ItemRequestDto(1L, "description", userDto, created, items);
        itemRequestDto2 = new ItemRequestDto(2L, "description2", userDto, created, items);
    }

    @Test
    void createItemRequest() throws Exception {
        long userId = 1L;
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void createItemRequest_whenUserIdNotFound_shouldReturnNotFoundException() throws Exception {
        long ownerId = 1000L;
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1L;
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getItemRequestById_whenUserIdNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1000L;
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllItemRequestByUser() throws Exception {
        long userId = 1L;
        when(itemRequestService.findAllItemRequestByUser(anyLong()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void findAllItemRequestByUser_whenUserIdNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1000L;
        when(itemRequestService.findAllItemRequestByUser(anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllItemRequest() throws Exception {
        long userId = 1L;
        when(itemRequestService.findAllItemRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void findAllItemRequest_whenUserIdNotFound_shouldReturnNotFoundException() throws Exception {
        long userId = 1000L;
        when(itemRequestService.findAllItemRequest(anyLong(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isNotFound());
    }
}