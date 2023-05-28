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

    @Override
    public Item save(Item item, long itemId) {
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
}
