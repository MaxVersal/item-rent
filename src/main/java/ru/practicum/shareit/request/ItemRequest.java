package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;

    String description;

    User requestor;

    LocalDateTime created;
}
