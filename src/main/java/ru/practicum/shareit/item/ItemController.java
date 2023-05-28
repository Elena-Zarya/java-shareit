package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
   @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received POST request: new item");
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable("itemId") long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received PATCH request: update item id {}", itemId);
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId) {
        log.info("Received GET request: get item by id {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemByUser(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received GET request: get all item by owner {}", ownerId);
        return itemService.getAllItemByUser(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam("text") String text) {
        return itemService.findItemsByText(text);
    }
}
