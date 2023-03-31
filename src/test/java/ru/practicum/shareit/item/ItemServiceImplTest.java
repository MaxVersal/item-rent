package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemServiceImplTest {

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RequestRepository requestRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @Test
    void checkItemWhenMissingRequiredFieldsThenThrowException() {
        Item item = new Item();
        item.setName("");
        item.setDescription("");
        assertThrows(IncorrectItemException.class, () -> itemService.checkItem(item));
    }

    @Test
    void checkItemWhenAllRequiredFieldsArePresent() {
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        assertDoesNotThrow(() -> itemService.checkItem(item));
    }

    @Test
    void getItemsByOwnerIdReturnsEmptyListWhenNoItems() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> items = itemService.getItemsByOwnerId(1L);
        assertTrue(items.isEmpty());
    }

    @Test
    void getItemsByOwnerIdReturnsListOfItems() {
        User user = new User();
        Item item = new Item();
        Item item2 = new Item();
        item.setId(1L);
        item2.setId(2L);
        user.getItems().add(item);
        user.getItems().add(item2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> items = itemService.getItemsByOwnerId(1L);
        assertEquals(2, items.size());
    }

    @Test
    void getItemsByOwnerIdReturnsSortedListById() {
        User user = new User();
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);
        Item item3 = new Item();
        item3.setId(3L);
        user.getItems().add(item1);
        user.getItems().add(item2);
        user.getItems().add(item3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> items = itemService.getItemsByOwnerId(1L);
        assertEquals(3, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals(2L, items.get(1).getId());
        assertEquals(3L, items.get(2).getId());
    }

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
    void updateItemWhenRequesterIsNotOwnerThenThrowException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setId(1L);
        itemDto.setRequestId(1L);
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
    }

    @Test
    void updateItemWhenRequesterIsOwner() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Книга");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);
        itemDto.setId(1L);
        itemDto.setRequestId(1L);
        Item item = new Item();
        item.setName("Книга");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setId(1L);
        User user = new User();
        user.getItems().add(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ItemDto updatedItem = itemService.updateItem(itemDto, 1L, 1L);
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
        assertEquals(itemDto.getId(), updatedItem.getId());
    }

    @Test
    void returnUpdatedItemAfterUpdate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Книга");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);
        itemDto.setId(1L);
        itemDto.setRequestId(1L);
        Item item = new Item();
        item.setName("Книга");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setId(1L);
        User user = new User();
        user.getItems().add(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ItemDto updatedItem = itemService.updateItem(itemDto, 1L, 1L);
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
        assertEquals(itemDto.getId(), updatedItem.getId());
    }

    @Test
    void updateItemWithPartialFields() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Книга");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);
        itemDto.setId(1L);
        itemDto.setRequestId(1L);
        Item item = new Item();
        item.setName("Книга");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setId(1L);
        User user = new User();
        user.getItems().add(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ItemDto updatedItem = itemService.updateItem(itemDto, 1L, 1L);
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
        assertEquals(itemDto.getId(), updatedItem.getId());
    }

    @Test
    void searchItemWhenTextIsBlank() {
        assertTrue(itemService.searchItem("", 1L).isEmpty());
    }

    @Test
    void searchItemWhenTextMatchesUnavailableItems() {
        Item item = new Item();
        item.setAvailable(false);
        item.setName("test");
        item.setDescription("test");
        when(itemRepository.search("test")).thenReturn(List.of(item));
        List<ItemDto> result = itemService.searchItem("test", 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemWhenTextMatchesAvailableItems() {
        String text = "test";
        Long ownerId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setName("test");
        item.setDescription("test");
        item.setAvailable(true);
        when(itemRepository.search(text)).thenReturn(List.of(item));
        List<ItemDto> result = itemService.searchItem(text, ownerId);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
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
}