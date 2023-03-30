
package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Autowired
    private ItemMapper mapper;

    @Test
    void getItemByIdWhenItemIdNotFoundThenThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L));
    }

    @Test
    void getItemByIdWhenItemIdIsValid() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Книга");
        item.setDescription("Книга по программированию");
        item.setAvailable(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ItemDto itemDto = itemService.getItemById(1L);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void addItemWhenRequestIdNotFoundThenThrowRequestNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RequestNotFoundException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void addItemWhenItemIsNotValidThenThrowIncorrectItemException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("");
        itemDto.setAvailable(null);
        assertThrows(IncorrectItemException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test

    void addItemWhenRequesterIdNotFoundThenThrowUserNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        Long requesterId = 1L;
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemService.addItem(itemDto, requesterId));
    }

    @Test
    void addItemWhenRequesterIdIsValidAndItemIsValid() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(new Item());
        ItemDto result = itemService.addItem(itemDto, 1L);
        assertNotNull(result);
    }
}