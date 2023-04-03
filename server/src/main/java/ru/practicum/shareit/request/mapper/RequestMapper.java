package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "requestId", source = "request", qualifiedByName = "mapId")
    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    Comment commentDtoToComment(CommentDto commentDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "authorName", expression = "java(comment.getUser().getName())")
    @Mapping(target = "created", source = "commentTime")
    CommentDto commentToCommentDto(Comment comment);

    Set<Comment> commentDtoSetToCommentSet(Set<CommentDto> set);

    @Named("mapId")
    default Long mapId(ItemRequest itemRequest) {
        if (itemRequest != null) {
            return itemRequest.getId();
        }
        return null;
    }

    Set<ItemDto> toSetDto(Set<Item> itemsSet);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "created", source = "created")
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> toDtos(List<ItemRequest> requests);
}
