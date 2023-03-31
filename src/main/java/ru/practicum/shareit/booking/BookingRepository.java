package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b from Booking b where b.booker.id = :id order by b.start DESC")
    Page<Booking> findPageAllByUserId(Long id, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.id in (select i.id from Item i where i.owner.id = :ownerId) order by b.start DESC")
    Page<Booking> findPageBookingsForOwner(Long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = :id")
    List<Booking> findAllByUserId(Long id);

    @Query(value = "select b from Booking b where b.item.id in (select i.id from Item i where i.owner.id = :ownerId)")
    List<Booking> findBookingsForOwner(Long ownerId);

    @Query(value = "select b from Booking b where b.item.id = :id")
    List<Booking> findAllForItem(Long id);

    @Query(value = "select b from Booking b where b.item.id = :itemId and b.booker.id = :userId")
    List<Booking> findBookingsFromUserToItemWithStatus(Long itemId, Long userId);


}
