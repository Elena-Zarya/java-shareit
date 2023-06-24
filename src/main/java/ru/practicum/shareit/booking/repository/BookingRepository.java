package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllBookingByBookerIdAndStatusOrderByStartDesc(Long userId, Status approved);

    Collection<Booking> findAllBookingByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime
            now, LocalDateTime now1);

    Collection<Booking> findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllBookingByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllBookingByOwner(Long ownerId);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date < ?2 and b.end_date > ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllBookingByOwnerCurrent(Long ownerId, LocalDateTime now);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.end_date < ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllBookingByOwnerPast(Long ownerId, LocalDateTime now);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date > ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllBookingByOwnerFuture(Long ownerId, LocalDateTime now);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.status LIKE ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllBookingByOwnerByStatus(Long ownerId, String waiting);

    List<Booking> findBookingByItemIdAndStartIsBeforeAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status
            approved);

    List<Booking> findBookingByItemIdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status
            approved);
}
