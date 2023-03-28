package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long requesterId);

    List<ItemDto> searchItem(String text, Long ownerId);

    ItemDto updateItem(ItemDto item, Long requesterId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwnerId(Long id);
}
