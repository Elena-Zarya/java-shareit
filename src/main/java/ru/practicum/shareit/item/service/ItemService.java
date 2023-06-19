package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    Collection<ItemDto> getAllItemByUser(Long ownerId);

    Collection<ItemDto> findItemsByText(String text);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}
