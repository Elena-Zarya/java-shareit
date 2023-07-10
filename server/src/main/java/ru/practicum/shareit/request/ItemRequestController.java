package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received POST request: new itemRequest");
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET request: get booking by id {}", requestId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllItemRequestByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET request: get all itemRequest by user {}", userId);
        return itemRequestService.findAllItemRequestByUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Received GET request: get all itemRequest");
        return itemRequestService.findAllItemRequest(userId, from, size);
    }
}
