package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.excrption.InvalidRequestException;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void start() {
        userDto = new UserDto(1L, "name", "user@email.com");
        itemDto = new ItemDto(1L, "name", "description", true, userDto, null,
                null, null, null);

    }

    @Test
    void addItem_shouldReturnItemDto() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(itemMapper.dtoToItem(any())).thenReturn(item);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDtoSaved = itemService.addItem(itemDto, 1L);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description", itemDtoSaved.getDescription());
        assertEquals(true, itemDtoSaved.getAvailable());
        assertEquals(userDto, itemDtoSaved.getOwner());

        verify(itemRepository, Mockito.times(1))
                .save(item);
    }

    @Test
    void addItem_shouldReturnItemDtoOnRequest() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "name", "user@email.com");
        User user2 = new User(2L, "name2", "user2@email.com");
        ItemRequest request = new ItemRequest(1L, "description", user2, created);
        Item item = new Item(1L, "name", "description", true, user, request);
        itemDto.setRequestId(1L);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(itemMapper.dtoToItem(any())).thenReturn(item);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.save(any())).thenReturn(item);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemDto itemDtoSaved = itemService.addItem(itemDto, 1L);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description", itemDtoSaved.getDescription());
        assertEquals(true, itemDtoSaved.getAvailable());
        assertEquals(userDto, itemDtoSaved.getOwner());
        assertEquals(1L, itemDtoSaved.getRequestId());

        verify(itemRepository, Mockito.times(1))
                .save(item);
    }

    @Test
    void addItem_requestNotFound_shouldReturnNotFoundException() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "name", "user@email.com");
        User user2 = new User(2L, "name2", "user2@email.com");
        ItemRequest request = new ItemRequest(1L, "description", user2, created);
        Item item = new Item(1L, "name", "description", true, user, request);
        itemDto.setRequestId(1L);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(itemMapper.dtoToItem(any())).thenReturn(item);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 1L));
        verify(itemRepository, never())
                .save(item);
    }

    @Test
    void addItem_ownerNotFound_shouldReturnNotFoundException() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);

        Mockito.when(userService.getUserById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 1000L));
        verify(itemRepository, never())
                .save(item);
    }

    @Test
    void updateItem_updateDescription_shouldReturnNewItemDto() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description2", true, user, null);
        itemDto.setDescription("description2");

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDtoSaved = itemService.updateItem(itemDto, 1L, 1L);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description2", itemDtoSaved.getDescription());
        assertEquals(true, itemDtoSaved.getAvailable());
        assertEquals(userDto, itemDtoSaved.getOwner());

        verify(itemRepository, Mockito.times(1))
                .save(item);
    }

    @Test
    void updateItem_itemNotFound_shouldReturnNotFoundException() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);

        Mockito.when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
        verify(itemRepository, never())
                .save(item);
    }

    @Test
    void updateItem_userNotOwner_shouldReturnNotFoundException() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description2", true, user, null);
        itemDto.setDescription("description2");

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 2L));
        verify(itemRepository, never())
                .save(item);
    }

    @Test
    void getItemById_shouldReturnItemDto() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);

        ItemDto itemDtoSaved = itemService.getItemById(1L, 1L);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description", itemDtoSaved.getDescription());
        assertEquals(true, itemDtoSaved.getAvailable());
        assertEquals(userDto, itemDtoSaved.getOwner());
    }

    @Test
    void getItemById_shouldReturnItemDtoWithComment() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);
        Comment comment = new Comment(1L, "comment", item, user, created);
        CommentDto commentDto = new CommentDto(1L, "comment", itemDto, userDto.getName(), created);

        List<Comment> commentList = new ArrayList();
        commentList.add(comment);
        List<CommentDto> commentDtoList = new ArrayList();
        commentDtoList.add(commentDto);

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(commentMapper.commentToDto(any())).thenReturn(commentDto);
        Mockito.when(commentRepository.findAllCommentByItemIdOrderByIdAsc(anyLong())).thenReturn(commentList);

        ItemDto itemDtoSaved = itemService.getItemById(1L, 1L);
        assertEquals(1L, itemDtoSaved.getId());
        assertEquals("name", itemDtoSaved.getName());
        assertEquals("description", itemDtoSaved.getDescription());
        assertEquals(true, itemDtoSaved.getAvailable());
        assertEquals(userDto, itemDtoSaved.getOwner());
        assertEquals(commentDtoList, itemDtoSaved.getComments());
    }

    @Test
    void getItemById_itemNotFound_shouldReturnNotFoundException() {
        User user = new User(1L, "name", "user@email.com");

        Mockito.when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void getAllItemByUser_shouldReturnItemDtoList() {
        int from = 0;
        int size = 1;
        User user = new User(1L, "name", "user@email.com");
        List<Item> itemList = List.of(
                new Item(1L, "name", "description", true, user, null)
        );

        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(itemList);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);

        Collection<ItemDto> itemsByUsers = itemService.getAllItemByUser(1L, from, size);

        assertEquals(1, itemsByUsers.size());
    }

    @Test
    void getAllItemByUser_whenOwnerNotFound_shouldReturnNotFoundException() {
        int from = 0;
        int size = 1;
        Mockito.when(userService.getUserById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getAllItemByUser(2L, from, size));
    }

    @Test
    void findItemsByText_findByName_shouldReturnItemDtoList() {
        String text = "name";
        int from = 0;
        int size = 1;
        User user = new User(1L, "name", "user@email.com");
        List<Item> itemList = List.of(
                new Item(1L, "name", "description", true, user, null)
        );

        Mockito.when(itemRepository.findAllByNameContainingIgnoreCaseAndAvailable(anyString(), anyBoolean(),
                any())).thenReturn(itemList);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);

        Collection<ItemDto> itemsByUsers = itemService.findItemsByText(text, from, size);

        assertEquals(1, itemsByUsers.size());
    }

    @Test
    void findItemsByText_findByDescription_shouldReturnItemDtoList() {
        String text = "description";
        int from = 0;
        int size = 1;
        User user = new User(1L, "name", "user@email.com");
        List<Item> itemList = List.of(
                new Item(1L, "name", "description", true, user, null)
        );

        Mockito.when(itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(anyString(), anyBoolean(),
                any())).thenReturn(itemList);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);

        Collection<ItemDto> itemsByUsers = itemService.findItemsByText(text, from, size);

        assertEquals(1, itemsByUsers.size());
    }

    @Test
    void findItemsByText_textIsEmpty_shouldReturnItemDtoListIsEmpty() {
        String text = "";
        int from = 0;
        int size = 1;

        Collection<ItemDto> itemsByUsers = itemService.findItemsByText(text, from, size);

        assertEquals(0, itemsByUsers.size());
    }

    @Test
    void findItemsByText_shouldReturnItemDtoListIsEmpty() {
        String text = "description";
        int from = 0;
        int size = 1;

        Mockito.when(itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(anyString(), anyBoolean(),
                any())).thenReturn(new ArrayList<>());

        Collection<ItemDto> itemsByUsers = itemService.findItemsByText(text, from, size);

        assertEquals(0, itemsByUsers.size());
    }

    @Test
    void createComment() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 12, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 13, 16, 0);
        LocalDateTime created = LocalDateTime.of(2023, 6, 18, 16, 0);
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);
        Booking booking = new Booking(1L, start, end, item, user, Status.APPROVED);
        Comment comment = new Comment(1L, "comment", item, user, created);
        CommentDto commentDto = new CommentDto(1L, "comment", null, null, null);

        List<Booking> bookingsByUser = List.of(booking);

        Mockito.when(bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class))).thenReturn(bookingsByUser);
        Mockito.when(commentMapper.dtoToComment(any())).thenReturn(comment);
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(commentMapper.commentToDto(any())).thenReturn(commentDto);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(userMapper.dtoToUser(any())).thenReturn(user);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        CommentDto commentDtoSaved = itemService.createComment(commentDto, 1L, 1L);
        assertEquals(1L, commentDtoSaved.getId());
        assertEquals(itemDto, commentDtoSaved.getItem());
        assertEquals("comment", commentDtoSaved.getText());
        assertEquals(userDto.getName(), commentDtoSaved.getAuthorName());
    }

    @Test
    void createComment_whenItemNotFound_shouldReturnInvalidRequestException() {
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);
        CommentDto commentDto = new CommentDto(1L, "comment", null, null, null);

        Mockito.when(bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(userMapper.dtoToUser(any())).thenReturn(user);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(InvalidRequestException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void createComment_whenTextIsEmpty_shouldReturnInvalidRequestException() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 12, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 13, 16, 0);
        User user = new User(1L, "name", "user@email.com");
        Item item = new Item(1L, "name", "description", true, user, null);
        Booking booking = new Booking(1L, start, end, item, user, Status.APPROVED);
        CommentDto commentDto = new CommentDto(1L, "", null, null, null);

        List<Booking> bookingsByUser = List.of(booking);

        Mockito.when(bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class))).thenReturn(bookingsByUser);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(userMapper.dtoToUser(any())).thenReturn(user);
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(InvalidRequestException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void getItemsByRequest_shouldReturnItemDto() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "name", "user@email.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, created);
        Item item = new Item(1L, "name", "description", true, user, itemRequest);

        Mockito.when(itemRepository.findAllByRequestIdOrderById(anyLong())).thenReturn(List.of(item));
        Mockito.when(itemMapper.itemToDto(any())).thenReturn(itemDto);

        Collection<ItemDto> itemsByRequest = itemService.getItemsByRequest(1L);

        assertEquals(1, itemsByRequest.size());
    }

    @Test
    void getItemsByRequest_shouldReturnListIsEmpty() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "name", "user@email.com");

        Mockito.when(itemRepository.findAllByRequestIdOrderById(anyLong())).thenReturn(new ArrayList<>());

        Collection<ItemDto> itemsByRequest = itemService.getItemsByRequest(1L);

        assertEquals(0, itemsByRequest.size());
    }
}