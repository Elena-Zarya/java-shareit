package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperImplTest {

    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private UserDto userDto;
    private User user;
    private LocalDateTime created;

    @BeforeEach
    void start() {
        created = LocalDateTime.of(2023, 6, 15, 15, 0);
        userDto = new UserDto(1L, "user", "user@email.com");
        user = new User(1L, "user", "user@email.com");
        List<ItemDto> items = List.of(new ItemDto(1L, "name", "description", true, userDto,
                null, null, null, 1L), new ItemDto(2L, "name2",
                "description2", true, userDto, null, null, null,
                1L));
        itemRequestDto = new ItemRequestDto(1L, "description", userDto, created, items);
        itemRequest = new ItemRequest(1L, "description", user, created);
    }

    @Test
    void dtoToItemRequest_thenItemRequestDtoValid_thenReturnItemRequest() {
        ItemRequest itemRequestSaved = itemRequestMapper.dtoToItemRequest(itemRequestDto);
        assertEquals(1L, itemRequestSaved.getId());
        assertEquals("description", itemRequestSaved.getDescription());
        assertEquals(user, itemRequestSaved.getRequestor());
        assertEquals(created, itemRequestSaved.getCreated());
    }

    @Test
    void dtoToItemRequest_whenItemRequestDtoIsnull_thenReturnNull() {
        ItemRequest itemRequestSaved = itemRequestMapper.dtoToItemRequest(null);
        assertNull(itemRequestSaved);
    }

    @Test
    void itemRequestToDto_thenItemRequestValid_thenReturnItemRequestDto() {
        ItemRequestDto itemRequestDtoSaved = itemRequestMapper.itemRequestToDto(itemRequest);
        assertEquals(1L, itemRequestDtoSaved.getId());
        assertEquals("description", itemRequestDtoSaved.getDescription());
        assertEquals(userDto, itemRequestDtoSaved.getRequestor());
        assertEquals(created, itemRequestDtoSaved.getCreated());
    }

    @Test
    void itemRequestToDto_whenItemRequestIsnull_thenReturnNull() {
        ItemRequestDto itemRequestDtoSaved = itemRequestMapper.itemRequestToDto(null);
        assertNull(itemRequestDtoSaved);
    }

    @Test
    void userDtoToUser_whenUserDtoIsnull_thenReturnNull() {
        User userSaved = itemRequestMapper.userDtoToUser(null);
        assertNull(userSaved);
    }

    @Test
    void userToUserDto() {
        UserDto userDtoSaved = itemRequestMapper.userToUserDto(null);
        assertNull(userDtoSaved);
    }
}