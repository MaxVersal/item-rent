package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.user.groups.Create;

import javax.validation.constraints.NotNull;
import java.util.Set;

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

    private BookingForItem nextBooking;

    private BookingForItem lastBooking;

    private Set<CommentDto> comments;

}
