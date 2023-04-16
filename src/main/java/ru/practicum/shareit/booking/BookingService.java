package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    Booking create(long userId, BookingDto bookingDto);

    Booking read(long userId, long bookingId);

    Collection<Booking> readAll(long userId);

    Booking update(long userId, long bookingId, BookingDto bookingDto);

    void delete(long userId, long bookingId);

}
