package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByItemIdIn(Set<Long> ids);

    Collection<Booking> findAllByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);

    List<Booking> findAllByBookerId(Long booker, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfter(Long booker, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long booker, LocalDateTime time1, LocalDateTime time2, Pageable page);

    List<Booking> findAllByBookerIdAndEndBefore(Long booker, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStatus(Long booker, BookingStatus state, Pageable page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1)")
    List<Booking> findAllForOwner(Long owner, Pageable page);

    @Query("select b from Booking b " +
            "where b.status =?1 " +
            "and b.item in (select i.id from Item i " +
            "where i.owner.id = ?2)")
    List<Booking> findAllForOwnerState(BookingStatus state, Long owner, Pageable page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.end <?2")
    List<Booking> findAllForOwnerPast(Long itemId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start <?2 " +
            "and b.end>?2")
    List<Booking> findAllForOwnerCurrent(Long itemId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start >?2")
    List<Booking> findAllForOwnerFuture(Long itemId, LocalDateTime time, Pageable page);

}
