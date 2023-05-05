package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem() != null ? booking.getItem().getId() : null);
        bookingDto.setBooker(booking.getBooker() != null ? booking.getBooker().getId() : null);
        bookingDto.setStatus(bookingDto.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
//        booking.setItem(bookingDto.getItem() != null ? booking.getItem().getId() : null);
//        booking.setBooker(booking.getBooker() != null ? booking.getBooker().getId() : null);
        booking.setStatus(BookingStatus.valueOf(bookingDto.getStatus()));
        return booking;
    }

}
