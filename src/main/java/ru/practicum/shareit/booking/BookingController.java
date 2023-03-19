package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController("/bookings")
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    @Qualifier("BookingServiceImpl")
    private final BookingService bookingService;

    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @RequestBody BookingAccept bookingAccept) throws Exception {
        return bookingService.postBooking(bookingAccept, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@PathVariable Long bookingId,
                                   @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                   @RequestParam(name = "approved") Boolean approve) {
        return bookingService.patchBooking(ownerId,approve,bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingService.getBookings(bookingId,requesterId);
    }

    @GetMapping
    public List<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                       @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookings(requesterId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsForOwner(ownerId,state);
    }
}
