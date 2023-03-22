package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "authorName", expression = "java(comment.getUser().getName())")
    @Mapping(target = "created", source = "commentTime")
    @Mapping(target = "text", source = "text")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "comments", source = "comments")
    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    Item toEntity(ItemDto itemDto);
}
