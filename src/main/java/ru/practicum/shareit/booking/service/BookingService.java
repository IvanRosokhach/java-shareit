package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    Booking create(long userId, BookingDto bookingDto);

    Booking read(long userId, long bookingId);

    Collection<Booking> readAll(long userId, BookingState state);

    Booking updateStatus(long userId, long bookingId, boolean approved);

    void delete(long userId, long bookingId);

    Collection<Booking> readForOwner(long userId, BookingState state);

}
