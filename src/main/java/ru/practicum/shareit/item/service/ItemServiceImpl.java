package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        String description = itemDto.getDescription();
        String name = itemDto.getName();
        Boolean available = itemDto.getAvailable();
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
        UserDto owner = userService.getUserById(ownerId);
        itemDto.setOwner(owner);
        Item item = itemMapper.dtoToItem(itemDto);
        Item itemSaved = itemRepository.save(item);
        return itemMapper.itemToDto(itemSaved);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        String nameNew = itemDto.getName();
        String descriptionNew = itemDto.getDescription();
        Boolean availableNew = itemDto.getAvailable();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null;
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
            return itemMapper.itemToDto(itemSaved);
        } else {
            log.info("user is not the owner of the item");
            throw new NotFoundException("user is not the owner of the item");
        }
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.info("Item " + itemId + " not found");
            throw new NotFoundException("Item " + itemId + " not found");
        }
        List<Comment> comments = commentRepository.findAllCommentByItemIdOrderByIdAsc(itemId);
        ItemDto itemDto = itemMapper.itemToDto(item.get());
        List<CommentDto> commentsDto = new ArrayList<>();
        if (comments.size() != 0) {
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
    public Collection<ItemDto> getAllItemByUser(Long ownerId) {
        Collection<ItemDto> itemsByOwner = new ArrayList<>();
        List<Item> itemsList = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        if (itemsList != null) {
            for (Item item : itemsList) {
                ItemDto itemDto = itemMapper.itemToDto(item);
                addLastBookingAndNextBooking(itemDto);
                itemsByOwner.add(itemDto);
            }
        }
        return itemsByOwner;
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text) {
        Collection<ItemDto> itemsByText = new ArrayList<>();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<Item> allItemsByName = itemRepository.findAllByNameContainingIgnoreCaseAndAvailable(text, true);
        Collection<Item> allItemsByDescription = itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(
                text, true);
        if (allItemsByName != null) {
            for (Item item : allItemsByName) {
                itemsByText.add(itemMapper.itemToDto(item));
            }
        }
        if (allItemsByDescription != null) {
            for (Item item : allItemsByDescription) {
                assert allItemsByName != null;
                if (!allItemsByName.contains(item)) {
                    itemsByText.add(itemMapper.itemToDto(item));
                }
            }
        }
        return itemsByText;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        UserDto userDto = userService.getUserById(userId);
        User user = userMapper.dtoToUser(userDto);
        ItemDto item = getItemById(itemId, userId);
        Collection<Booking> bookingsByUser = bookingRepository.findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(
                userId, LocalDateTime.now());
        List<Booking> bookingsByUserByItem = bookingsByUser.stream().filter(booking -> Objects.equals(booking.getItem().
                getId(), itemId)).collect(Collectors.toList());
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

    private ItemDto addLastBookingAndNextBooking(ItemDto itemDto) {
        Long itemId = itemDto.getId();
        List<Booking> lastBookingsByItem = bookingRepository.findBookingByItemIdAndStartIsBeforeAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), Status.APPROVED);
        if (lastBookingsByItem.size() > 0) {
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