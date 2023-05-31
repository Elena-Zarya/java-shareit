package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> itemsByOwner = new HashMap<>();
    private long id;

    @Override
    public Item save(Item item) {
        generateId();
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Item findItemById(long itemId) {
        try {
            return items.get(itemId);
        } catch (Exception e) {
            throw new NotFoundException("item id " + itemId + " not found");
        }
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @Override
    public void setItemsByOwner(long userId, List<Long> itemsList) {
        itemsByOwner.put(userId, itemsList);
    }

    @Override
    public List<Long> getItemsByOwner(long ownerId) {
        return itemsByOwner.get(ownerId);
    }

    private long generateId() {
        return ++id;
    }
}
