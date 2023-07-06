package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentMapperImplTest {

    @InjectMocks
    private CommentMapperImpl commentMapper;
    private Item item;
    private ItemDto itemDto;
    private LocalDateTime created;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void start() {
        User user = new User(1L, "name", "user@email.com");
        item = new Item(1L, "name", "description", true, user, null);
        UserDto userDto = new UserDto(1L, "name", "user@email.com");
        itemDto = new ItemDto(1L, "name", "description", true, userDto, null,
                null, null, null);
        created = LocalDateTime.of(2023, 6, 7, 15, 0);
        commentDto = new CommentDto(1L, "comment", itemDto, userDto.getName(), created);
        comment = new Comment(1L, "comment", item, user, created);
    }

    @Test
    void dtoToComment() {
        Comment commentSaved = commentMapper.dtoToComment(commentDto);
        assertEquals(1L, commentSaved.getId());
        assertEquals("comment", commentSaved.getText());
        assertEquals(item, commentSaved.getItem());
        assertEquals(created, commentSaved.getCreated());
    }

    @Test
    void dtoToComment_whenCommentDtoIsNull_shouldReturnNull() {
        Comment commentSaved = commentMapper.dtoToComment(null);
        assertNull(commentSaved);
    }

    @Test
    void commentToDto() {
        CommentDto commentDtoSaved = commentMapper.commentToDto(comment);
        assertEquals(1L, commentDtoSaved.getId());
        assertEquals("comment", commentDtoSaved.getText());
        assertEquals(itemDto, commentDtoSaved.getItem());
        assertEquals(created, commentDtoSaved.getCreated());
    }

    @Test
    void commentToDto_whenCommentIsNull_shouldReturnNull() {
        CommentDto commentDtoSaved = commentMapper.commentToDto(null);
        assertNull(commentDtoSaved);
    }

    @Test
    void userDtoToUser_whenUserDtoIsNull_shouldReturnNull() {
        User userSaved = commentMapper.userDtoToUser(null);
        assertNull(userSaved);
    }

    @Test
    void itemDtoToItem() {
        Item itemSaved = commentMapper.itemDtoToItem(null);
        assertNull(itemSaved);
    }

    @Test
    void userToUserDto() {
        UserDto userDtoSaved = commentMapper.userToUserDto(null);
        assertNull(userDtoSaved);
    }

    @Test
    void itemToItemDto() {
        ItemDto itemDtoSaved = commentMapper.itemToItemDto(null);
        assertNull(itemDtoSaved);
    }
}