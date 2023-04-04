package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingAccept bookingAccept, Long bookerId);

    BookingDto patchBooking(Long requesterId, Boolean approved, Long bookingId);

    BookingDto getBookingById(Long bookingId, Long requesterId);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state, Pageable pageable);

    ItemDto setLastAndNextBooking(ItemDto itemDto);

    List<BookingDto> findAllBookingsWithParametres(Long requesterId, Pageable pageable, String state);
}
