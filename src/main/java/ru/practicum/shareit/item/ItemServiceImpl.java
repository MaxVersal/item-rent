package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper mapper;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long requesterId) {
        Item item = mapper.toEntity(itemDto);
        checkItem(item);
        if (userRepository.findById(requesterId).isPresent()) {
            userRepository.findById(requesterId).get().getItems().add(item);
            return mapper.toDto(itemRepository.save(item));
        } else {
            throw new UserNotFoundException("Не найден владелец");
        }

    }

    @Override
    public List<ItemDto> searchItem(String text, Long ownerId) {
        if (text.isBlank()) {
            return Collections.emptyList(); //постман при пустом тексте просит вернуть пустую коллекцию...
        }
        List<ItemDto> searchResult = new ArrayList<>();
        for (Item item : itemRepository.search(text)) {
            if (item.getAvailable()) {
                searchResult.add(mapper.toDto(item));
            }
        }
        return searchResult;
    }

    @Override
    public ItemDto updateItem(ItemDto item, Long requesterId, Long itemId) {
        for (Item itemCurrent : userRepository.findById(requesterId).get().getItems()) {
            if (itemCurrent.getId().equals(itemId)) {
                itemRepository
                        .findById(itemId)
                        .ifPresent(item1 -> {
                            if (item.getAvailable() != null) {
                                item1.setAvailable(item.getAvailable());
                            }
                            if (item.getName() != null) {
                                item1.setName(item.getName());
                            }
                            if (item.getDescription() != null) {
                                item1.setDescription(item.getDescription());
                            }
                            itemRepository.save(item1);
                        });
                return mapper.toDto(itemRepository.findById(itemId).get());
            }
        }
        throw new ItemNotFoundException("Обновлять предмет может только ее владелец");
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        try {
            return mapper.toDto(itemRepository.findById(itemId).get());
        } catch (Exception e) {
            throw new ItemNotFoundException("Не найден предмет с указанным id");
        }

    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long id) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : userRepository.findById(id).get().getItems()) {
            items.add(mapper.toDto(item));
        }
        return items.stream()
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    private static void checkItem(Item item) {
        if (item.getAvailable() == null ||
                item.getName() == null || item.getName().isBlank() ||
                item.getDescription() == null) {
            throw new IncorrectItemException("Некорректно создан предмет");
        }
    }
}
