package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Validated BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public Booking read(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable long bookingId) {
        return bookingService.read(userId, bookingId);
    }

    @GetMapping
    public Collection<Booking> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.readAll(userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long bookingId,
                          @RequestBody BookingDto bookingDto) {
        return bookingService.update(userId, bookingId, bookingDto);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long bookingId) {
        bookingService.delete(bookingId);
    }

}
