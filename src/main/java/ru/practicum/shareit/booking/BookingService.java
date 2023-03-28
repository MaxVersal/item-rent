package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingAccept bookingAccept, Long bookerId);

    BookingDto patchBooking(Long requesterId, Boolean approved, Long bookingId);

    BookingDto getBookings(Long bookingId, Long requesterId);

    List<BookingDto> getBookings(Long requesterId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);

    ItemDto setLastAndNextBooking(ItemDto itemDto);
}
