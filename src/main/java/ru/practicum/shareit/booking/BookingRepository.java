package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * from bookings where user_id = ?1",
            nativeQuery = true)
    List<Booking> findAllByUserId(Long id);

    @Query(value = "select * from bookings where item_id in (" +
            "select id from items where user_id = ?1)", nativeQuery = true)
    List<Booking> findBookingsForOwner(Long ownerId);

    @Query(value = "select * from bookings where item_id = ?1",
            nativeQuery = true)
    List<Booking> findAllForItem(Long id);

    @Query(value = "select * from bookings where item_id = ?1 and user_id = ?2",
            nativeQuery = true)
    List<Booking> findBookingsFromUserToItemWithStatus(Long itemId, Long userId);
}
