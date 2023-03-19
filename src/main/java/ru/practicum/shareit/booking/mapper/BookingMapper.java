package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@AllArgsConstructor
public class BookingMapper {
    private UserMapper userMapper;
    private ItemMapper itemMapper;

    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userMapper.toUserBooking(booking.getBooker()),
                itemMapper.toItemBooking(booking.getItem()));
    }

    public Booking toEntity(BookingAccept bookingAccept) {
        return new Booking(bookingAccept.getStart(),
                bookingAccept.getEnd());
    }

    public BookingForItem toBookingItem(Booking booking) {
        return new BookingForItem(booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd());
    }
}
