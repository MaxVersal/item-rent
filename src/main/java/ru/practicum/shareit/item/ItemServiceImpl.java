package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public ItemDto postItem(ItemDto itemDto, Long ownerId) {
        return mapper.toItemDto(itemRepository.addItem(mapper.toItem(itemDto,ownerId)));
    }

    @Override
    public List<ItemDto> searchItem(String text, Long ownerId) {
        List<ItemDto> searchResult = new ArrayList<>();
        for (Item item : itemRepository.searchItem(text, ownerId)) {
            searchResult.add(mapper.toItemDto(item));
        }
        return searchResult;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        if (!itemRepository.getOwnerIdWithItemId(itemId).equals(ownerId)) {
            throw new ItemNotFoundException("Обновлять предмет может только ее владелец");
        } else {
            itemDto.setId(itemId);
            return mapper.toItemDto(itemRepository.updateItem(mapper.toItem(itemDto,ownerId)));
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return mapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long id) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.getItemsByOwnerId(id)) {
            items.add(mapper.toItemDto(item));
        }
        return items;
    }
}
