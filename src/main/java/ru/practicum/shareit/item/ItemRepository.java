package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    void addUser(Long id);

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getItemsByOwnerId(Long id);

    List<Item> searchItem(String searchText, Long ownerId);
}
