package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.excrption.InvalidRequestException;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.Pages;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        Long requestId = itemDto.getRequestId();
        UserDto owner = userService.getUserById(ownerId);
        itemDto.setOwner(owner);
        Item item = itemMapper.dtoToItem(itemDto);
        if (requestId != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
            if (itemRequest.isEmpty()) {
                throw new NotFoundException("itemRequest " + requestId + " not found");
            }
            item.setRequest(itemRequest.get());
        }
        Item itemSaved = itemRepository.save(item);
        ItemDto itemDtoSaved = itemMapper.itemToDto(itemSaved);
        if (itemSaved.getRequest() != null) {
            itemDtoSaved.setRequestId(itemSaved.getRequest().getId());
        }
        return itemDtoSaved;
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        String nameNew = itemDto.getName();
        String descriptionNew = itemDto.getDescription();
        Boolean availableNew = itemDto.getAvailable();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        if (Objects.equals(item.getOwner().getId(), ownerId)) {
            if (descriptionNew != null) {
                item.setDescription(descriptionNew);
            }
            if (nameNew != null) {
                item.setName(nameNew);
            }
            if (availableNew != null) {
                item.setAvailable(availableNew);
            }
            Item itemSaved = itemRepository.save(item);
            ItemDto itemDtoNew = itemMapper.itemToDto(itemSaved);
            addLastBookingAndNextBooking(itemDtoNew);
            return itemDtoNew;
        } else {
            log.info("user is not the owner of the item");
            throw new NotFoundException("user is not the owner of the item");
        }
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        List<Comment> comments = commentRepository.findAllCommentByItemIdOrderByIdAsc(itemId);
        ItemDto itemDto = itemMapper.itemToDto(item);
        List<CommentDto> commentsDto = new ArrayList<>();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                CommentDto commentDto = commentMapper.commentToDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentsDto.add(commentDto);
            }
        }
        itemDto.setComments(commentsDto);
        if (Objects.equals(userId, itemDto.getOwner().getId())) {
            addLastBookingAndNextBooking(itemDto);
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllItemByUser(Long ownerId, int from, int size) {
        Pageable page = Pages.getPage(from, size);

        UserDto owner = userService.getUserById(ownerId);
        List<Item> itemsList = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, page);

        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemsList) {
            ItemDto itemDto = itemMapper.itemToDto(item);
            addLastBookingAndNextBooking(itemDto);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text, int from, int size) {
        Pageable page = Pages.getPage(from, size);

        Collection<ItemDto> itemsByText = new ArrayList<>();
        if (text.isEmpty()) {
            return itemsByText;
        }
        List<Item> allItemsByName = itemRepository.findAllByNameContainingIgnoreCaseAndAvailable(text, true, page);
        List<Item> allItemsByDescription = itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(
                text, true, page);
        if (allItemsByName != null) {
            for (Item item : allItemsByName) {
                itemsByText.add(itemMapper.itemToDto(item));
            }
        }
        if (allItemsByDescription != null) {
            for (Item item : allItemsByDescription) {
                if (!allItemsByName.contains(item)) {
                    itemsByText.add(itemMapper.itemToDto(item));
                }
            }
        }
        for (ItemDto itemDto : itemsByText) {
            addLastBookingAndNextBooking(itemDto);
        }
        return itemsByText;
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        UserDto userDto = userService.getUserById(userId);
        User user = userMapper.dtoToUser(userDto);
        ItemDto item = getItemById(itemId, userId);
        addLastBookingAndNextBooking(item);
        LocalDateTime created = LocalDateTime.now();
        Collection<Booking> bookingsByUser = bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(
                userId, created);
        List<Booking> bookingsByUserByItem = bookingsByUser.stream().filter(booking -> Objects.equals(booking.getItem()
                .getId(), itemId)).collect(Collectors.toList());
        if (bookingsByUserByItem.isEmpty()) {
            log.info("item not found");
            throw new InvalidRequestException("item not found");
        }

        String text = commentDto.getText();
        if (text.isEmpty()) {
            log.info("comment is empty");
            throw new InvalidRequestException("comment is empty");
        }
        commentDto.setItem(item);
        commentDto.setText(text);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.dtoToComment(commentDto);
        comment.setAuthor(user);
        Comment commentSaved = commentRepository.save(comment);
        CommentDto commentDtoSaved = commentMapper.commentToDto(commentSaved);
        commentDtoSaved.setAuthorName(commentSaved.getAuthor().getName());
        return commentDtoSaved;
    }

    @Override
    public List<ItemDto> getItemsByRequest(long requestId) {
        List<ItemDto> itemsByRequest = new ArrayList<>();
        List<Item> itemsList = itemRepository.findAllByRequestIdOrderById(requestId);
        if (itemsList != null) {
            for (Item item : itemsList) {
                ItemDto itemToRequestDto = itemMapper.itemToDto(item);
                itemToRequestDto.setRequestId(item.getRequest().getId());
                addLastBookingAndNextBooking(itemToRequestDto);
                itemsByRequest.add(itemToRequestDto);
            }
        }
        return itemsByRequest;
    }

    private ItemDto addLastBookingAndNextBooking(ItemDto itemDto) {
        Long itemId = itemDto.getId();
        List<Booking> lastBookingsByItem = bookingRepository.findBookingByItemIdAndStartIsBeforeAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), Status.APPROVED);
        if (!lastBookingsByItem.isEmpty()) {
            Booking lastBooking = lastBookingsByItem.get(lastBookingsByItem.size() - 1);

            BookingDtoForItem lastBookingDto = bookingMapper.bookingToDtoForItem(lastBooking);
            lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            itemDto.setLastBooking(lastBookingDto);
        }
        List<Booking> nextBookingsByItem = bookingRepository.findBookingByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), Status.APPROVED);
        if (nextBookingsByItem.size() > 0) {
            Booking nextBooking = nextBookingsByItem.get(0);
            BookingDtoForItem nextBookingDto = bookingMapper.bookingToDtoForItem(nextBooking);
            nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            itemDto.setNextBooking(nextBookingDto);
        }
        return itemDto;
    }
}