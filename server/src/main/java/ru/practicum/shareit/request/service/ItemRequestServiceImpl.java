package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.excrption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.Pages;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        String description = itemRequestDto.getDescription();

        if (description == null || description.isEmpty()) {
            log.info("description is empty");
            throw new ValidationException("description is empty");
        }
        UserDto user = userService.getUserById(userId);
        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.dtoToItemRequest(itemRequestDto);
        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.itemRequestToDto(itemRequestSaved);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, long userId) {
        UserDto user = userService.getUserById(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            log.info("itemRequest " + requestId + " not found");
            throw new NotFoundException("itemRequest " + requestId + " not found");
        }
        ItemRequestDto itemRequestDto = itemRequestMapper.itemRequestToDto(itemRequest.get());
        List<ItemDto> itemDto = itemService.getItemsByRequest(requestId);
        itemRequestDto.setItems(itemDto);

        return itemRequestDto;
    }

    @Override
    public Collection<ItemRequestDto> findAllItemRequestByUser(long userId) {
        UserDto user = userService.getUserById(userId);
        Collection<ItemRequest> itemRequestsByUser = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        Collection<ItemRequestDto> itemRequestsByUserDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestsByUser) {
            ItemRequestDto itemRequestDto = itemRequestMapper.itemRequestToDto(itemRequest);
            List<ItemDto> itemToRequestDto = itemService.getItemsByRequest(itemRequest.getId());
            itemRequestDto.setItems(itemToRequestDto);
            itemRequestsByUserDto.add(itemRequestDto);
        }
        return itemRequestsByUserDto;
    }

    @Override
    public Collection<ItemRequestDto> findAllItemRequest(long userId, int from, int size) {
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest page = Pages.getPage(from, size, sortByCreated);

        List<ItemRequestDto> allItemRequests = itemRequestRepository.findAll(userId, page).stream()
                .map(itemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : allItemRequests) {
            List<ItemDto> itemToRequestDto = itemService.getItemsByRequest(itemRequestDto.getId());
            itemRequestDto.setItems(itemToRequestDto);
        }
        return allItemRequests;
    }
}
