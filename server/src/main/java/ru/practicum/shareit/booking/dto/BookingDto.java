package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.user.dto.UserBooking;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long id;

    LocalDateTime start;

    LocalDateTime end;

    Status status;

    UserBooking booker;

    ItemBooking item;

}
