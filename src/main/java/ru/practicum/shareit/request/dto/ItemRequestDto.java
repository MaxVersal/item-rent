package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;

    @NotNull
    String description;

    LocalDateTime created = LocalDateTime.now();

    Set<ItemDto> items = new HashSet<>();
}
