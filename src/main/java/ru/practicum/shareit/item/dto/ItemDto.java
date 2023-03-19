package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingForItem;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
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

    private BookingForItem nextBooking;

    private BookingForItem lastBooking;

    private Set<CommentDto> comments;
}
