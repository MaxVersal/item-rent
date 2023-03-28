package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBooking;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    Booking toEntity(BookingAccept bookingAccept);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingForItem toBookingItem(Booking booking);

    @Mapping(target = "id", source = "id")
    UserBooking toUserBooking(User user);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "id", source = "id")
    ItemBooking toItemBooking(Item item);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "booker", expression = "java(toUserBooking(booking.getBooker()))")
    @Mapping(target = "item", expression = "java(toItemBooking(booking.getItem()))")
    BookingDto toDto(Booking booking);

}
