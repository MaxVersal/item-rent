package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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

    @Autowired
    private final RequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long requesterId) {
        Item item = mapper.toEntity(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Не найден запрос"));
            item.setRequest(itemRequest);
        }
        checkItem(item);
        if (userRepository.findById(requesterId).isPresent()) {
            userRepository.findById(requesterId).get().getItems().add(item);
            item.setOwner(userRepository.findById(requesterId).get());
            return mapper.toDto(itemRepository.save(item));
        } else {
            throw new UserNotFoundException("Не найден владелец");
        }

    }

    @Override
    public List<ItemDto> searchItem(String text, Long ownerId, Integer from, Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList(); //постман при пустом тексте просит вернуть пустую коллекцию...
        }
        List<ItemDto> searchResult = new ArrayList<>();
        for (Item item : itemRepository.search(text, PageRequest.of(from / size, size))) {
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

    static void checkItem(Item item) {
        if (item.getAvailable() == null ||
                item.getName() == null || item.getName().isBlank() ||
                item.getDescription() == null) {
            throw new IncorrectItemException("Некорректно создан предмет");
        }
    }
}
