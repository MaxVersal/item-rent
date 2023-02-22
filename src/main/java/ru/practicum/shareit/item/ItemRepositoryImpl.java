package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static Map<Long, List<Item>> userItems = new HashMap<>();

    private static Map<Long, Item> items = new HashMap<>();

    private static Long newId = 1L;

    @Override
    public void addUser(Long id) {
        userItems.put(id, new ArrayList<>());
    }

    @Override
    public Item addItem(Item item) {
        if (userItems.containsKey(item.getOwnerId())) {
            item.setId(newId++);
            userItems.get(item.getOwnerId()).add(item);
            items.put(item.getId(), item);
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

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new ItemNotFoundException("Предмет с данным айди не найден");
        }
    }


    @Override
    public List<Item> getItemsByOwnerId(Long id) {
        return userItems.get(id);
    }

    @Override
    public List<Item> searchItem(String searchText, Long ownerId) {
        if (searchText.isBlank()) {
            return Collections.emptyList(); //постман при пустом тексте просит вернуть пустую коллекцию...
        }
        List<Item> result = new ArrayList<>();
        searchText = searchText.toLowerCase();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText)) &&
                        item.getAvailable() == true) {
                    result.add(item);
            }
        }
        return result;
    }
}
