package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllBookingByBookerIdAndStatusOrderByStartDesc(Long userId, Status approved, Pageable page);

    List<Booking> findAllBookingByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findAllBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime
            now, LocalDateTime now1, Pageable page);

    List<Booking> findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable page);

    Collection<Booking> findAllBookingByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllBookingByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable page);

    @Query(value = "select b.* " +
            "from bookings AS b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingByOwner(Long ownerId, Pageable page);

    @Query(value = "select b.* " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date < ?2 and b.end_date > ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingByOwnerCurrent(Long ownerId, LocalDateTime now, Pageable page);

    @Query(value = "select b.* " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.end_date < ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingByOwnerPast(Long ownerId, LocalDateTime now, Pageable page);

    @Query(value = "select b.* " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date > ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingByOwnerFuture(Long ownerId, LocalDateTime now, Pageable page);

    @Query(value = "select b.* " +
            "from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.status LIKE ?2 " +
            "group by b.id " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingByOwnerByStatus(Long ownerId, String waiting, Pageable page);

    List<Booking> findBookingByItemIdAndStartIsBeforeAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status
            approved);

    List<Booking> findBookingByItemIdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status
            approved);
}
