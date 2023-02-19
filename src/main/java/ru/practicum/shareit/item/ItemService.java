package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto postItem(ItemDto itemDto, Long ownerId);

    List<ItemDto> searchItem(String text, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwnerId(Long id);
}
