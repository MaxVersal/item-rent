package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ItemDto {
    @NotNull(groups = Create.class)
    String name;

    @NotNull(groups = Create.class)
    String description;

    @NotNull(groups = Create.class)
    Boolean available;

    Long id;


    Long requestId;
}