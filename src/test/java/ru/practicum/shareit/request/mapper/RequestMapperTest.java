package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RequestMapperTest {
    @Autowired
    private RequestMapper requestMapper;

    @Test
    @DisplayName("should map Item to ItemDto correctly")
    void toDtoMapsItemToItemDtoCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        Set<Comment> comments = new HashSet<>();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCommentTime(LocalDateTime.now());
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email");
        comment.setUser(user);
        comments.add(comment);
        item.setComments(comments);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        item.setRequest(itemRequest);
        User owner = new User();
        owner.setId(1L);
        owner.setName("name");
        owner.setEmail("email");
        item.setOwner(owner);
        ItemDto itemDto = requestMapper.toDto(item);
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getRequest().getId());
        assertThat(itemDto.getComments().size()).isEqualTo(item.getComments().size());
        assertThat(itemDto.getComments().stream().map(CommentDto::getId).collect(Collectors.toList())).isEqualTo(item.getComments().stream().map(Comment::getId).collect(Collectors.toList()));
        assertThat(itemDto.getComments().stream().map(CommentDto::getText).collect(Collectors.toList())).isEqualTo(item.getComments().stream().map(Comment::getText).collect(Collectors.toList()));
        assertThat(itemDto.getComments().stream().map(CommentDto::getAuthorName).collect(Collectors.toList())).isEqualTo(item.getComments().stream().map(comment1 -> comment1.getUser().getName()).collect(Collectors.toList()));
        assertThat(itemDto.getComments().stream().map(CommentDto::getCreated).collect(Collectors.toList())).isEqualTo(item.getComments().stream().map(Comment::getCommentTime).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("should convert a list of ItemRequest entities to a list of ItemRequestDto objects")
    void toDtosConvertsListOfItemRequestEntitiesToItemRequestDtos() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("description1");
        itemRequest1.setCreated(LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("description2");
        itemRequest2.setCreated(LocalDateTime.now());
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);
        itemRequests.add(itemRequest2);
        List<ItemRequestDto> itemRequestDtos = requestMapper.toDtos(itemRequests);
        assertThat(itemRequestDtos).isNotNull();
        assertThat(itemRequestDtos.size()).isEqualTo(2);
        assertThat(itemRequestDtos.get(0).getId()).isEqualTo(1L);
        assertThat(itemRequestDtos.get(0).getDescription()).isEqualTo("description1");
        assertThat(itemRequestDtos.get(1).getId()).isEqualTo(2L);
        assertThat(itemRequestDtos.get(1).getDescription()).isEqualTo("description2");
    }

    @Test
    @DisplayName("should map ItemRequest to ItemDto correctly")
    void toDtoMapsItemRequestToItemDtoCorrectly() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        Set<Item> items = new HashSet<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        Set<Comment> comments = new HashSet<>();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCommentTime(LocalDateTime.now());
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email");
        comment.setUser(user);
        comments.add(comment);
        item.setComments(comments);
        items.add(item);
        itemRequest.setItems(items);
        ItemRequestDto itemRequestDto = requestMapper.toDto(itemRequest);
        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(itemRequestDto.getItems()).isNotNull();
        assertThat(itemRequestDto.getItems().size()).isEqualTo(1);
        ItemDto itemDto = itemRequestDto.getItems().stream().findFirst().get();
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getComments()).isNotNull();
        assertThat(itemDto.getComments().size()).isEqualTo(1);
        CommentDto commentDto = itemDto.getComments().stream().findFirst().get();
        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCommentTime());
    }

    @Test
    @DisplayName("should convert a set of CommentDto objects to a set of Comment objects")
    void commentDtoSetToCommentSet() {
        Set<CommentDto> commentDtoSet = new HashSet<>();
        commentDtoSet.add(new CommentDto(1L, "authorName", LocalDateTime.now(), "text"));
        commentDtoSet.add(new CommentDto(2L, "authorName", LocalDateTime.now(), "text"));
        commentDtoSet.add(new CommentDto(3L, "authorName", LocalDateTime.now(), "text"));
        Set<Comment> commentSet = requestMapper.commentDtoSetToCommentSet(commentDtoSet);
        assertThat(commentSet).isNotNull();
        assertThat(commentSet.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("should map Comment to CommentDto with correct fields")
    void commentToCommentDtoMapping() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCommentTime(LocalDateTime.now());
        User user = new User();
        user.setName("name");
        comment.setUser(user);
        CommentDto commentDto = requestMapper.commentToCommentDto(comment);
        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getUser().getName());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCommentTime());
    }

    @Test
    @DisplayName("should map CommentDto to Comment with correct fields")
    void commentDtoToCommentMapping() {
        CommentDto commentDto = new CommentDto(1L, "author", LocalDateTime.now(), "text");
        Comment comment = requestMapper.commentDtoToComment(commentDto);
        assertThat(comment.getId()).isEqualTo(commentDto.getId());
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
    }

    @Test
    @DisplayName("should map a set of Item objects to a set of ItemDto objects")
    void toSetDtoMapsSetOfItemsToSetOfItemDtos() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setComments(new HashSet<>());
        item1.setRequest(new ItemRequest());
        item1.setOwner(new User());

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setDescription("description2");
        item2.setAvailable(false);
        item2.setComments(new HashSet<>());
        item2.setRequest(new ItemRequest());
        item2.setOwner(new User());

        Set<Item> items = new HashSet<>();
        items.add(item1);
        items.add(item2);

        Set<ItemDto> itemDtos = requestMapper.toSetDto(items);

        assertThat(itemDtos).isNotNull();

    }

    @Test
    @DisplayName("should convert a set of Item objects to a set of ItemDto objects")
    void toSetDtoConvertsItemSetToItemDtoSet() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setDescription("description2");
        item2.setAvailable(false);
        Set<Item> items = new HashSet<>();
        items.add(item1);
        items.add(item2);
        Set<ItemDto> itemDtos = requestMapper.toSetDto(items);
        assertThat(itemDtos).isNotNull();
    }

    @Test
    @DisplayName("should convert ItemRequestDto to ItemRequest with correct fields")
    void toEntityConvertsItemRequestDtoToItemRequestWithCorrectFields() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        Set<ItemDto> items = new HashSet<>();
        items.add(itemDto);
        itemRequestDto.setItems(items);
        ItemRequest itemRequest = requestMapper.toEntity(itemRequestDto);
        assertThat(itemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemRequest.getCreated()).isEqualTo(itemRequestDto.getCreated());
        assertThat(itemRequest.getItems().size()).isEqualTo(itemRequestDto.getItems().size());
    }

    @Test
    @DisplayName("should map Item with null request to ItemDto with null requestId")
    void toDtoMapsItemWithNullRequestToItemDtoWithNullRequestId() {
        Comment comment1 = new Comment("test2", new User());
        Comment comment = new Comment("test", new User());
        CommentDto commentDto = new CommentDto(1L, ".", LocalDateTime.now(), "test");
        CommentDto commentDto1 = new CommentDto(2L, ".", LocalDateTime.now(), "test2");
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setRequest(null);
        item.setComments(new HashSet<>());
        item.getComments().add(comment);
        item.getComments().add(comment1);
        ItemDto itemDto = requestMapper.toDto(item);
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
        assertEquals(itemDto.getComments().stream().collect(Collectors.toList()).get(0).getText(), commentDto.getText());
        assertEquals(itemDto.getComments().stream().collect(Collectors.toList()).get(1).getText(), commentDto1.getText());
    }

    @Test
    @DisplayName("should map Item with non-null request to ItemDto with requestId")
    void toDtoMapsItemWithNonNullRequestToItemDtoWithRequestId() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item.setRequest(itemRequest);
        ItemDto itemDto = requestMapper.toDto(item);
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getRequestId()).isEqualTo(itemRequest.getId());
    }

}
