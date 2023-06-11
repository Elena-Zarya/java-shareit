package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        String description = itemDto.getDescription();
        String name = itemDto.getName();
        Boolean available = itemDto.getAvailable();
        userService.checkUserId(ownerId);
        if (description == null || description.isEmpty()) {
            log.info("description is empty");
            throw new ValidationException("description is empty");
        }
        if (name == null || name.isEmpty()) {
            log.info("name is empty");
            throw new ValidationException("name is empty");
        }
        if (available == null) {
            log.info("available is empty");
            throw new ValidationException("available is empty");
        }
        if (ownerId == 0) {
            log.info("owner of the item is not specified");
            throw new ValidationException("owner of the item is not specified");
        }
        Item item = itemMapper.dtoToItem(itemDto);
        Item itemSaved = itemStorage.save(item);
        long itemId = itemSaved.getId();
        userService.addItem(itemId, ownerId);
        UserDto owner = userService.getUserById(ownerId);
        item.setOwner(userMapper.dtoToUser(owner));
        itemStorage.setItemsByOwner(ownerId, owner.getItemsList());
        return itemMapper.itemToDto(itemSaved);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long ownerId) {
        String nameNew = itemDto.getName();
        String descriptionNew = itemDto.getDescription();
        Boolean availableNew = itemDto.getAvailable();
        Item item = itemStorage.findItemById(itemId);
        if (item.getOwner().getId() == ownerId) {
            if (descriptionNew != null) {
                item.setDescription(descriptionNew);
            }
            if (nameNew != null) {
                item.setName(nameNew);
            }
            if (availableNew != null) {
                item.setAvailable(availableNew);
            }
            Item itemSaved = itemStorage.updateItem(item, itemId);
            return itemMapper.itemToDto(itemSaved);
        } else {
            log.info("user is not the owner of the item");
            throw new NotFoundException("user is not the owner of the item");
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemStorage.findItemById(itemId);
        return itemMapper.itemToDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemByUser(long ownerId) {
        Collection<ItemDto> itemsByOwner = new ArrayList<>();
        List<Long> itemsIdList = itemStorage.getItemsByOwner(ownerId);
        if (itemsIdList != null) {
            for (Long itemId : itemsIdList) {
                itemsByOwner.add(itemMapper.itemToDto(itemStorage.findItemById(itemId)));
            }
        }
        return itemsByOwner;
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text) {
        Collection<ItemDto> itemsByText = new ArrayList<>();
        Collection<Item> allItems = text.isEmpty() ? new ArrayList<>() : itemStorage.getAllItems();
        for (Item item : allItems) {
            if (item.getAvailable() && (item.getName().toUpperCase().contains(text.toUpperCase())
                    || item.getDescription().toUpperCase().contains(text.toUpperCase()))) {
                itemsByText.add(itemMapper.itemToDto(item));
            }
        }
        return itemsByText;
    }
}
