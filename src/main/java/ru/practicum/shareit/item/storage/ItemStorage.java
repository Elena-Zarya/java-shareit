package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item save(Item item, long itemId);

    Item findItemById(long itemId);

    Collection<Item> getAllItems();
}
