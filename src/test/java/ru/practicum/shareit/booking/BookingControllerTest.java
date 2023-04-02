package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private BookingAccept bookingAccept = new BookingAccept();

    private BookingDto bookingDto = new BookingDto();

    @BeforeEach
    public void setUp() {
        bookingAccept.setItemId(1L);
        bookingAccept.setStart(LocalDateTime.of(2024, 12, 12, 5, 5));
        bookingAccept.setEnd(LocalDateTime.of(2025, 12, 12, 5, 5));
        bookingDto.setId(1L);
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setStart(bookingAccept.getStart());
        bookingDto.setEnd(bookingAccept.getEnd());
    }

    @Test
    @DisplayName("should post booking")
    public void shouldPostBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content("{\"itemId\" : \"1\", \"start\" : \"2024-12-12T05:05:00\", \"end\" : \"2025-12-12T05:05:00\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$.end").value("2025-12-12T05:05:00"));

    }

    @Test
    @DisplayName("should patch booking")
    public void shouldPatchBookingStatus() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.patchBooking(anyLong(), any(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$.end").value("2025-12-12T05:05:00"));
    }

    @Test
    @DisplayName("should return booking with id")
    public void shouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$.end").value("2025-12-12T05:05:00"));
    }

    @Test
    @DisplayName("returning bookins with pages params")
    public void shouldReturnbookings() throws Exception {
        when(bookingService.findAllBookingsWithParametres(anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .param("from", "2")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$[0].end").value("2025-12-12T05:05:00"));

    }

    @Test
    @DisplayName("returning bookins with pages params for booking owner")
    public void shouldReturnbookingsForOwner() throws Exception {
        when(bookingService.findAllBookingsForOwnerWithParametres(anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .param("from", "2")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$[0].end").value("2025-12-12T05:05:00"));

    }

    @Test
    @DisplayName("should throw exception with wrong page params for owner")
    public void shouldThrowWrongPageDataForOwner() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .param("from", "-2")
                        .param("size", "2"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("should throw exception with wrong page params")
    public void shouldThrowWrongPageData() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .param("from", "-2")
                        .param("size", "2"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("get booking for owner without params")
    public void shouldReturnBookinsForOwnerWithoutParams() throws Exception {
        when(bookingService.getBookingsForOwner(anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$[0].end").value("2025-12-12T05:05:00"));
    }

    @Test
    @DisplayName("get bookings for booker without params")
    public void shouldReturnBookingsForBookerWithoutParams() throws Exception {
        when(bookingService.getBookings(anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").value("2024-12-12T05:05:00"))
                .andExpect(jsonPath("$[0].end").value("2025-12-12T05:05:00"));
    }
}