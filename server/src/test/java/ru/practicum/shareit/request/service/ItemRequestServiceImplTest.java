package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.excrption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private UserDto userDto;
    private List<ItemDto> items;

    @BeforeEach
    void start() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
        userDto = new UserDto(1L, "user", "user@email.com");
        User user = new User(1L, "name", "user@email.com");
        items = List.of(new ItemDto(1L, "name", "description", true, userDto,
                null, null, null, 1L), new ItemDto(2L, "name2",
                "description2", true, userDto, null, null, null,
                1L));
        itemRequestDto = new ItemRequestDto(1L, "description", userDto, created, items);
        itemRequest = new ItemRequest(1L, "description", user, created);
    }


    @Test
    void createItemRequest_shouldReturnItemRequestDto() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(itemRequestMapper.dtoToItemRequest(any())).thenReturn(itemRequest);
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        Mockito.when(itemRequestMapper.itemRequestToDto(any())).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDtoSaved = itemRequestService.createItemRequest(itemRequestDto, 1L);
        assertEquals(1L, itemRequestDtoSaved.getId());
        assertEquals("description", itemRequestDtoSaved.getDescription());
        assertNotNull(itemRequestDtoSaved.getRequestor());
        assertNotNull(itemRequestDtoSaved.getCreated());
        assertNotNull(itemRequestDtoSaved.getItems());

        verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequest);
    }

    @Test
    void createItemRequest_whenDescriptionIsEmpty_shouldReturnValidationException() {
        itemRequestDto.setDescription(null);

        assertThrows(ValidationException.class, () -> itemRequestService.createItemRequest(itemRequestDto, 1L));
        verify(itemRequestRepository, never())
                .save(itemRequest);
    }

    @Test
    void getItemRequestById_shouldReturnItemRequestDto() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRequestMapper.itemRequestToDto(any())).thenReturn(itemRequestDto);
        Mockito.when(itemService.getItemsByRequest(anyLong())).thenReturn(items);

        ItemRequestDto itemRequestDtoSaved = itemRequestService.getItemRequestById(1L, 1L);
        assertEquals(1L, itemRequestDtoSaved.getId());
        assertEquals("description", itemRequestDtoSaved.getDescription());
        assertNotNull(itemRequestDtoSaved.getRequestor());
        assertNotNull(itemRequestDtoSaved.getCreated());
        assertNotNull(itemRequestDtoSaved.getItems());

        verify(itemRequestRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_shouldReturnNotFoundException() {
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
        verify(itemRequestRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void findAllItemRequestByUser() {
        List<ItemRequest> itemRequestList = List.of(itemRequest);

        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(itemRequestList);
        Mockito.when(itemRequestMapper.itemRequestToDto(any()))
                .thenReturn(itemRequestDto);
        Mockito.when(itemService.getItemsByRequest(anyLong()))
                .thenReturn(items);

        Collection<ItemRequestDto> itemRequestDtoCollectionByUsers = itemRequestService.findAllItemRequestByUser(1L);

        assertEquals(1, itemRequestDtoCollectionByUsers.size());
    }

    @Test
    void findAllItemRequestByUser_shouldReturnCollectionIsEmpty() {
        List<ItemRequest> itemRequestList = new ArrayList<>();

        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(itemRequestList);

        Collection<ItemRequestDto> itemRequestDtoCollectionByUsers = itemRequestService.findAllItemRequestByUser(1L);

        assertEquals(0, itemRequestDtoCollectionByUsers.size());
    }

    @Test
    void findAllItemRequest() {
        int from = 0;
        int size = 1;
        List<ItemRequest> itemRequestList = List.of(itemRequest);

        Mockito.when(itemRequestRepository.findAll(anyLong(), any())).thenReturn(itemRequestList);
        Mockito.when(itemRequestMapper.itemRequestToDto(any())).thenReturn(itemRequestDto);
        Mockito.when(itemService.getItemsByRequest(anyLong())).thenReturn(items);

        Collection<ItemRequestDto> itemRequestsByUsers = itemRequestService.findAllItemRequest(1L, from, size);

        assertEquals(1, itemRequestsByUsers.size());
    }
}