package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    @DisplayName("should map Booking entity to BookingDto correctly")
    void toDtoMapsBookingEntityToBookingDtoCorrectly() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2020, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2020, 1, 2, 0, 0));
        booking.setStatus(Status.APPROVED);
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@mail.ru");
        booking.setBooker(user);
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        booking.setItem(item);
        BookingDto bookingDto = bookingMapper.toDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDto.getItem().getName());
    }

    @Test
    @DisplayName("should map Booking to BookingForItem correctly")
    void toBookingItemMapping() {
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now());
        booking.setId(1L);
        booking.setBooker(new User());
        booking.getBooker().setId(2L);
        BookingForItem bookingForItem = bookingMapper.toBookingItem(booking);
        assertEquals(booking.getId(), bookingForItem.getId());
        assertEquals(booking.getBooker().getId(), bookingForItem.getBookerId());
        assertEquals(booking.getStart(), bookingForItem.getStart());
        assertEquals(booking.getEnd(), bookingForItem.getEnd());
    }

    @Test
    @DisplayName("should map BookingAccept to Booking entity")
    void toEntityMapsBookingAcceptToBooking() {
        BookingAccept bookingAccept =
                new BookingAccept(1L, LocalDateTime.now(), LocalDateTime.now());
        Item item = new Item();
        item.setId(1L);
        Booking booking = bookingMapper.toEntity(bookingAccept);
        booking.setItem(item);
        assertEquals(bookingAccept.getItemId(), booking.getItem().getId());
        assertEquals(bookingAccept.getStart(), booking.getStart());
        assertEquals(bookingAccept.getEnd(), booking.getEnd());
    }
}