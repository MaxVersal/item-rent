package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper mapper;

    private final ItemRepositoryImpl itemRepository;

    @Override
    public ItemDto addItem(Item item, Long ownerId) {
        item.setOwnerId(ownerId);
        checkItem(item);
        return mapper.toDto(itemRepository.addItem(item));
    }

    @Override
    public List<ItemDto> searchItem(String text, Long ownerId) {
        List<ItemDto> searchResult = new ArrayList<>();
        for (Item item : itemRepository.searchItem(text, ownerId)) {
            searchResult.add(mapper.toDto(item));
        }
        return searchResult;
    }

    @Override
    public ItemDto updateItem(Item item, Long ownerId, Long itemId) {
        item.setOwnerId(ownerId);
        if (!itemRepository.getItemById(itemId).getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("Обновлять предмет может только ее владелец");
        } else {
            item.setId(itemId);
            return mapper.toDto(itemRepository.updateItem(item));
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return mapper.toDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long id) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.getItemsByOwnerId(id)) {
            items.add(mapper.toDto(item));
        }
        return items;
    }

    private static void checkItem(Item item) {
        if (item.getAvailable() == null ||
                item.getOwnerId() == null ||
                item.getName() == null || item.getName().isBlank() ||
                item.getDescription() == null) {
            throw new IncorrectItemException("Некорректно создан предмет");
        }
    }
}
