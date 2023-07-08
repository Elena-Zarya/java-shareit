package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRequestMapper {
    ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto itemRequestToDto(ItemRequest itemRequest);
}
