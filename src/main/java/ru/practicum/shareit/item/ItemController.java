package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Received POST request: new item");
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @PathVariable("itemId") Long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received PATCH request: update item id {}", itemId);
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET request: get item by id {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemByUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Received GET request: get all item by owner {}", ownerId);
        return itemService.getAllItemByUser(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@Valid @RequestParam("text") String text) {
        log.info("Received GET request: get all item by text {}", text);
        return itemService.findItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received POST request: new comment");
        return itemService.createComment(commentDto, userId, itemId);
    }
}
