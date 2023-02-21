package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, Long ownerId);

    List<ItemDto> searchItem(String text, Long ownerId);

    ItemDto updateItem(Item item, Long ownerId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwnerId(Long id);
}
