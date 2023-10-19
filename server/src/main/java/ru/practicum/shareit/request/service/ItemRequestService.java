package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId);

    ItemRequestDto getItemRequestById(Long requestId, long userId);

    Collection<ItemRequestDto> findAllItemRequestByUser(long userId);

    Collection<ItemRequestDto> findAllItemRequest(long userId, int from, int size);
}
