package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static Map<Long, List<Item>> items = new HashMap<>();

    private static Long newId = 1L;

    @Override
    public void addUser(Long id) {
        items.put(id, new ArrayList<>());
    }

    @Override
    public Item addItem(Item item) {
        checkItem(item);
        if (items.containsKey(item.getOwnerId())) {
            item.setId(newId++);
            items.get(item.getOwnerId()).add(item);
            return item;
        } else {
            throw new UserNotFoundException("Пользователя не существует");
        }

    }

    @Override
    public Item updateItem(Item item) {
        if (item.getDescription() != null) {
            getItemById(item.getId()).setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            getItemById(item.getId()).setName(item.getName());
        }
        if (item.getAvailable() != null) {
            getItemById(item.getId()).setAvailable(item.getAvailable());
        }
        return getItemById(item.getId());
    }


    private static void checkItem(Item item) {
        if (item.getAvailable() == null ||
                item.getOwnerId() == null ||
                item.getName() == null || item.getName().isBlank() ||
                item.getDescription() == null) {
            throw new IncorrectItemException("Некорректно создан предмет");
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        List<Item> allItems = items.get(getOwnerIdWithItemId(itemId));
        for (Item currentItem : allItems) {
            if (currentItem.getId().equals(itemId)) {
                return currentItem;
            }
        }
        throw new ItemNotFoundException("Предмет с данным айди не найден");
    }


    @Override
    public List<Item> getItemsByOwnerId(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> searchItem(String searchText, Long ownerId) {
        if (searchText.isBlank()) {
            return Collections.emptyList(); //постман при пустом тексте просит вернуть пустую коллекцию...
        }
        List<Item> result = new ArrayList<>();
        searchText = searchText.toLowerCase();
        for (List<Item> value : items.values()) {
            for (Item item : value) {
                if ((item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText)) &&
                        item.getAvailable() == true) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public Long getOwnerIdWithItemId(Long id) {
        List<Item> allItems = new ArrayList<>();
        for (List<Item> value : items.values()) {
            allItems.addAll(value);
        }
        Item currentItem = null;
        for (Item item : allItems) {
            if (item.getId().equals(id)) {
                currentItem = item;
            }
        }
        if (currentItem == null) {
            throw new ItemNotFoundException("Не найден данный предмет");
        } else {
            return currentItem.getOwnerId();
        }
    }

}
