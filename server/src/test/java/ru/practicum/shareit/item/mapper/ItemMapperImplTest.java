package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperImplTest {

    @InjectMocks
    private ItemMapperImpl itemMapper;
    private User user;
    private Item item;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "user@email.com");
        item = new Item(1L, "name", "description", true, user, null);
        userDto = new UserDto(1L, "name", "user@email.com");
        itemDto = new ItemDto(1L, "name", "description", true, userDto, null,
                null, null, null);
    }

    @Test
    void dtoToItem() {
        Item itemSaved = itemMapper.dtoToItem(itemDto);
        assertEquals(1L, itemSaved.getId());
        assertEquals("name", itemSaved.getName());
        assertEquals("description", itemSaved.getDescription());
        assertEquals(user, itemSaved.getOwner());
        assertEquals(true, itemSaved.getAvailable());
    }

    @Test
    void dtoToItem_whenItemDtoIsNull_thenReturnNull() {
        Item itemSaved = itemMapper.dtoToItem(null);
        assertNull(itemSaved);
    }

    @Test
    void itemToDto() {
        ItemDto itemDtoSaved = itemMapper.itemToDto(item);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description", itemDtoSaved.getDescription());
        assertEquals(userDto, itemDtoSaved.getOwner());
        assertEquals(true, itemDtoSaved.getAvailable());
    }

    @Test
    void itemToDto_whenItemIsNull_thendReturnNull() {
        ItemDto itemDtoSaved = itemMapper.itemToDto(null);
        assertNull(itemDtoSaved);
    }

    @Test
    void userDtoToUser() {
        User userSaved = itemMapper.userDtoToUser(null);
        assertNull(userSaved);
    }

    @Test
    void userToUserDto() {
        UserDto userDtoSaved = itemMapper.userToUserDto(null);
        assertNull(userDtoSaved);
    }
}