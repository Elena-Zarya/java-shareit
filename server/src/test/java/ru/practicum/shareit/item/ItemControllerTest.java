package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void start() {
        UserDto userDto = new UserDto(1L, "user", "user@email.com");
        itemDto = new ItemDto(null, "name", "description", true, userDto,
                null, null, null, null);
        commentDto = new CommentDto(1L, "text", itemDto, userDto.getName(), null);
    }

    @Test
    void createItem_shouldReturnStatusOkWithItemDto() throws Exception {
        long ownerId = 1L;
        when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void createItem_ownerNotFound_shouldReturnStatusNotFound() throws Exception {
        long ownerId = 1000L;
        when(itemService.addItem(any(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_shouldReturnStatusOkWithItemDto() throws Exception {
        long ownerId = 1L;
        long itemId = 1L;
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void updateItem_itemIdNotFound_shouldReturnStatusNotFound() throws Exception {
        long ownerId = 1L;
        long itemId = 1L;
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById_shouldReturnStatusOkWithItemDto() throws Exception {
        long ownerId = 1L;
        long itemId = 1L;
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void getItemById_itemNotFound_shouldReturnStatusNotFound() throws Exception {
        long ownerId = 1L;
        long itemId = 1000L;
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemByUser_shouldReturnStatusOkWithItemDtoCollection() throws Exception {
        long ownerId = 1L;
        when(itemService.getAllItemByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    void getAllItemByUser_shouldReturnStatusOkWithItemDtoCollectionIsEmpty() throws Exception {
        long ownerId = 1L;
        when(itemService.getAllItemByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllItemByUser_ownerNotFound_shouldReturnStatusNotFound() throws Exception {
        long ownerId = 1000L;
        when(itemService.getAllItemByUser(anyLong(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findItemsByText_shouldReturnStatusOkWithItemDtoCollection() throws Exception {
        String text = "text";
        when(itemService.findItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    void findItemsByText_shouldReturnStatusOkWithItemDtoCollectionIsEmpty() throws Exception {
        String text = "";
        when(itemService.findItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createComment_shouldReturnStatusOkWithCommentDto() throws Exception {
        long itemId = 1L;
        long userId = 2L;

        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);
        commentDto.setId(1L);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void createComment_commentIsEmpty_shouldReturnStatusBadRequest() throws Exception {
        long itemId = 1L;
        long userId = 2L;

        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenThrow(InvalidRequestException.class);
        commentDto.setId(1L);
        commentDto.setText("");

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}