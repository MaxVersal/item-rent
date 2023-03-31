package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("BookingServiceImpl")
class BookingServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("should return rejected bookings for owner when state is REJECTED")
    void getBookingsForOwnerWhenStateIsRejected() {
        User owner = new User();
        owner.setId(1L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.REJECTED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsForOwner(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(1L, "REJECTED");
        assertEquals(1, bookings.size());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    @DisplayName("should return waiting bookings for owner when state is WAITING")
    void getBookingsForOwnerWhenStateIsWaiting() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        owner.getItems().add(item);
        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsForOwner(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(1L, "WAITING");
        assertEquals(1, bookings.size());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsForOwner(anyLong());
    }

    @Test
    @DisplayName("should return future bookings for owner when state is FUTURE")
    void getBookingsForOwnerWhenStateIsFuture() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsForOwner(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(1L, "FUTURE");
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
    }

    @Test
    @DisplayName("should return past bookings for owner when state is PAST")
    void getBookingsForOwnerWhenStateIsPast() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        owner.getItems().add(item);
        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().minusDays(1).plusHours(1));
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsForOwner(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(1L, "PAST");
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());

        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
    }

    @Test
    @DisplayName("should return all bookings for owner when state is ALL")
    void getBookingsForOwnerWhenStateIsAll() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        owner.getItems().add(item);
        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsForOwner(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(1L, "ALL");
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
    }

    @Test
    @DisplayName(
            "should throw WrongDataUpdateException when booking start time is equal to end time")
    void checkBookingWhenStartTimeIsEqualToEndTimeThrowsWrongDataUpdateException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        assertThrows(WrongDataUpdateException.class, () -> bookingService.checkBooking(booking));
    }

    @Test
    @DisplayName("should throw WrongDataUpdateException when booking end time is before start time")
    void checkBookingWhenEndTimeIsBeforeStartTimeThrowsWrongDataUpdateException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now());
        assertThrows(WrongDataUpdateException.class, () -> bookingService.checkBooking(booking));
    }

    @Test
    @DisplayName(
            "should throw WrongDataUpdateException when booking end time is before current time")
    void checkBookingWhenEndTimeIsBeforeCurrentTimeThrowsWrongDataUpdateException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now());
        assertThrows(WrongDataUpdateException.class, () -> bookingService.checkBooking(booking));
    }

    @Test
    @DisplayName("should throw ItemAlreadyInUseException when item is not available")
    void checkBookingWhenItemIsNotAvailableThrowsItemAlreadyInUseException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        Item item = new Item();
        item.setAvailable(false);
        booking.setItem(item);
        assertThrows(ItemAlreadyInUseException.class, () -> bookingService.checkBooking(booking));
    }

    @Test
    @DisplayName(
            "should throw WrongDataUpdateException when booking start time is before current time")
    void checkBookingWhenStartTimeIsBeforeCurrentTimeThrowsWrongDataUpdateException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setItem(new Item());
        booking.setBooker(new User());
        assertThrows(WrongDataUpdateException.class, () -> bookingService.checkBooking(booking));
    }

    @Test
    @DisplayName("should return past bookings for the given user")
    void getBookingsForPastState() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findAllByUserId(anyLong())).thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookings(1L, "PAST").size());
    }

    @Test
    @DisplayName("should return future bookings for the given user")
    void getBookingsForFutureState() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        when(bookingRepository.findAllByUserId(anyLong())).thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookings(1L, "FUTURE").size());
    }

    @Test
    @DisplayName("should return all bookings for the given user")
    void getBookingsForAllState() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        when(bookingRepository.findAllByUserId(anyLong())).thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookings(1L, "ALL").size());
    }

    @Test
    @DisplayName("should return waiting bookings for the given user")
    void getBookingsForWaitingState() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserId(anyLong())).thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookings(1L, "WAITING").size());
    }

    @Test
    @DisplayName("should return rejected bookings for the given user")
    void getBookingsForRejectedState() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.REJECTED);
        booking.setBooker(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserId(anyLong())).thenReturn(List.of(booking));
        assertEquals(
                bookingService.getBookings(1L, "REJECTED").get(0).getStatus(), Status.REJECTED);
    }

    @Test
    @DisplayName(
            "should throw NoSuchElementException when the requester is neither the booker nor the item owner")
    void getBookingWhenRequesterIsNotBookerOrItemOwnerThenThrowException() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User requester = new User();
        requester.setId(2L);
        requester.setName("requester");
        requester.setEmail("requester@mail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("item");
        item.setAvailable(true);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        assertThrows(NoSuchElementException.class, () -> bookingService.getBooking(1L, 2L));
    }

    @Test
    @DisplayName("should return the booking when the requester is the booker")
    void getBookingWhenRequesterIsBooker() {
        User booker = new User();
        booker.setId(1L);
        Item item = new Item();
        item.setId(1L);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        BookingDto bookingDto = bookingService.getBooking(1L, 1L);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
        assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    @DisplayName("should return the booking when the requester is the item owner")
    void getBookingWhenRequesterIsItemOwner() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDto bookingDto = bookingService.getBooking(1L, 1L);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
        assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    @DisplayName("should throw a NoSuchElementException when the requester is not the item owner")
    void patchBookingWhenRequesterNotItemOwnerThenThrowNoSuchElementException() {
        User user = new User();
        User user1 = new User();
        user.setId(1L);
        user1.setId(2L);
        Item item = new Item();
        item.setId(1L);
        user.getItems().add(item);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NoSuchElementException.class, () -> bookingService.patchBooking(2L, true, 1L));
    }

    @Test
    @DisplayName(
            "should update the booking status to REJECTED when the requester is the item owner and the booking status is not already updated")
    void patchBookingWhenRequesterIsItemOwnerAndStatusNotUpdatedThenReject() {
        Long requesterId = 1L;
        Long bookingId = 1L;
        User user1 = new User();
        user1.setId(2L);
        Boolean approved = false;
        User requester = new User();
        requester.setId(requesterId);
        Item item = new Item();
        item.setId(1L);
        requester.getItems().add(item);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user1);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDto bookingDto = bookingService.patchBooking(requesterId, approved, bookingId);
        assertEquals(bookingDto.getStatus(), Status.REJECTED);
    }

    @Test
    @DisplayName(
            "should update the booking status to APPROVED when the requester is the item owner and the booking status is not already updated")
    void patchBookingWhenRequesterIsItemOwnerAndStatusNotUpdatedThenApprove() {
        Long requesterId = 1L;
        Long bookingId = 1L;
        User user = new User();
        user.setId(2L);
        Boolean approved = true;
        User requester = new User();
        requester.setId(requesterId);
        Item item = new Item();
        item.setId(1L);
        requester.getItems().add(item);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDto bookingDto = bookingService.patchBooking(requesterId, approved, bookingId);
        assertEquals(Status.APPROVED, bookingDto.getStatus());
    }

    @Test
    @DisplayName(
            "should throw an IllegalArgumentException when the booking status is already updated")
    void patchBookingWhenStatusAlreadyUpdatedThenThrowIllegalArgumentException() {
        User user = new User();
        user.setId(1L);
        User user1 = new User();
        user1.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        user1.getItems().add(item);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertThrows(
                NoSuchElementException.class, () -> bookingService.patchBooking(1L, true, 1L));
    }

    @Test
    @DisplayName("should throw ItemNotFoundException when itemId is not found")
    void createBookingWhenItemIdNotFoundThenThrowItemNotFoundException() {
        User user = new User();
        BookingAccept bookingAccept =
                new BookingAccept(1L, LocalDateTime.now(), LocalDateTime.now());
        when(itemRepository.findById(bookingAccept.getItemId())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(
                ItemNotFoundException.class, () -> bookingService.createBooking(bookingAccept, 1L));
    }

    @Test
    @DisplayName("should throw UserNotFoundException when bookerId is not found")
    void createBookingWhenBookerIdNotFoundThenThrowUserNotFoundException() {
        BookingAccept bookingAccept =
                new BookingAccept(1L, LocalDateTime.now(), LocalDateTime.now());
        Long bookerId = 1L;
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> bookingService.createBooking(bookingAccept, bookerId));
    }

    @Test
    @DisplayName("should throw IncorrectBookingException when checkBooking fails")
    void createBookingWhenCheckBookingFailsThenThrowIncorrectBookingException() {
        BookingAccept bookingAccept =
                new BookingAccept(1L, LocalDateTime.now(), LocalDateTime.now());
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.save(booking)).thenReturn(booking);
        assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.createBooking(bookingAccept, 1L));
    }

    @Test
    @DisplayName("should throw SelfBookingException when booker tries to book their own item")
    void createBookingWhenBookerTriesToBookOwnItemThenThrowSelfBookingException() {
        BookingAccept bookingAccept =
                new BookingAccept(1L, LocalDateTime.now(), LocalDateTime.now());
        Long bookerId = 1L;
        User booker = new User();
        booker.setId(bookerId);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        booker.getItems().add(item);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingAccept.getItemId())).thenReturn(Optional.of(item));
        Throwable exception =
                assertThrows(
                        SelfBookingException.class,
                        () -> bookingService.createBooking(bookingAccept, bookerId));
        assertEquals("Нельзя бронировать вещь у себя же!", exception.getMessage());
    }
}