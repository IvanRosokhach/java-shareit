package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Override
    public Booking create(long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public Booking read(long userId, long bookingId) {
        return null;
    }

    @Override
    public Collection<Booking> readAll(long userId) {
        return null;
    }

    @Override
    public Booking update(long userId, long bookingId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public void delete(long bookingId) {

    }

}
