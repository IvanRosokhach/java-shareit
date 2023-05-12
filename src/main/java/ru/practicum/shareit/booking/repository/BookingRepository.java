package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByItemIdIn(Set<Long> ids);

    Collection<Booking> findAllByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);

    Collection<Booking> findAllByBookerId(Long booker, PageRequest page);

    Collection<Booking> findAllByBookerIdAndStartAfter(Long booker, LocalDateTime time, PageRequest page);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long booker, LocalDateTime time1, LocalDateTime time2, PageRequest page);

    Collection<Booking> findAllByBookerIdAndEndBefore(Long booker, LocalDateTime time, PageRequest page);

    Collection<Booking> findAllByBookerIdAndStatus(Long booker, BookingStatus state, PageRequest page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1)")
    Collection<Booking> findAllForOwner(Long owner, PageRequest page);

    @Query("select b from Booking b " +
            "where b.status =?1 " +
            "and b.item in (select i.id from Item i " +
            "where i.owner.id = ?2)")
    Collection<Booking> findAllForOwnerState(BookingStatus state, Long owner, PageRequest page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.end <?2")
    Collection<Booking> findAllForOwnerPast(Long itemId, LocalDateTime time, PageRequest page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start <?2 " +
            "and b.end>?2")
    Collection<Booking> findAllForOwnerCurrent(Long itemId, LocalDateTime time, PageRequest page);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start >?2")
    Collection<Booking> findAllForOwnerFuture(Long itemId, LocalDateTime time, PageRequest page);

}
