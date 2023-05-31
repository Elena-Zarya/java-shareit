package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Item save(Item item);
    Item updateItem(Item item, long itemId);

    Item findItemById(long itemId);

    Collection<Item> getAllItems();

    void setItemsByOwner(long userId, List<Long> itemsList);
    List<Long> getItemsByOwner(long ownerId);
}
