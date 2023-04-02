package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    @DisplayName("should return null when itemRequest is null")
    void mapIdWhenItemRequestIsNull() {
        assertNull(itemMapper.mapId(null));
    }

    @Test
    @DisplayName("should return itemRequest id when itemRequest is not null")
    void mapIdWhenItemRequestIsNotNull() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        Long id = itemMapper.mapId(itemRequest);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("should map Item to ItemDto correctly")
    void toDtoMapsItemToItemDtoCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCommentTime(LocalDateTime.now());
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email");
        comment.setUser(user);
        item.setComments(Set.of(comment));
        ItemDto itemDto = itemMapper.toDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getComments().size(), itemDto.getComments().size());
        assertEquals(
                item.getComments().iterator().next().getId(),
                itemDto.getComments().iterator().next().getId());
        assertEquals(
                item.getComments().iterator().next().getText(),
                itemDto.getComments().iterator().next().getText());
        assertEquals(
                item.getComments().iterator().next().getCommentTime(),
                itemDto.getComments().iterator().next().getCreated());
        assertEquals(
                item.getComments().iterator().next().getUser().getName(),
                itemDto.getComments().iterator().next().getAuthorName());
    }

    @Test
    @DisplayName("should map Comment to CommentDto correctly")
    void toCommentDtoMapping() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCommentTime(LocalDateTime.now());
        User user = new User();
        user.setName("name");
        comment.setUser(user);
        CommentDto commentDto = itemMapper.toCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCommentTime(), commentDto.getCreated());
        assertEquals(comment.getUser().getName(), commentDto.getAuthorName());
    }
}