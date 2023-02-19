package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @NonNull
    String name;

    @NonNull
    String description;

    @NonNull
    Boolean available;

    @NonNull
    Long id;


    public ItemDto(String name, String description, Boolean available, Long id) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.id = id;
    }

}
