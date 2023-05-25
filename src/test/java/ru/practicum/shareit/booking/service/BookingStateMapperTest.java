package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotAvailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingStateMapperTest {

    @Autowired
    private BookingStateMapper bookingStateMapper;

    @Test
    void toBookingStateWhenInvokeThenReturnBookingState() {
        BookingState result = BookingStateMapper.toBookingState("ALL");

        assertEquals(BookingState.ALL, result);
    }

    @Test
    void toBookingStateWhenNoValidStateThenThrowException() {
        assertThrows(NotAvailableException.class, () -> BookingStateMapper.toBookingState("INCORRECT"));
    }

}