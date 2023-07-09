package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("New itemRequest from user id {}", userId);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") Long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking by id {}", requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemRequestByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all itemRequest by user {}", userId);
        return itemRequestClient.findAllItemRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all itemRequest");
        return itemRequestClient.findAllItemRequest(userId, from, size);
    }
}
