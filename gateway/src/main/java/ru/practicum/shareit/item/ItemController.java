package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Post new item by ownerId {}", ownerId);
        return itemClient.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable("itemId") long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Update item id {}", itemId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by id {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemByUser(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item by owner {}, from={}, size={}", ownerId, from, size);
        return itemClient.getAllItemByUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam("text") String text,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item by text {}", text);
        return itemClient.findItemsByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add new comment");
        return itemClient.createComment(commentDto, userId, itemId);
    }
}
