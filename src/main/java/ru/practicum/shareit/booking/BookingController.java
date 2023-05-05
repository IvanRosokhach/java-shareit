package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Validated BookingDto bookingDto) {
        log.debug("{} create", this.getClass().getName());
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public Booking read(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable long bookingId) {
        log.debug("{} read({})", this.getClass().getName(), bookingId);
        return bookingService.read(userId, bookingId);
    }

    @GetMapping
    public Collection<Booking> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("{} readAll for userId:{}.", this.getClass().getName(), userId);
        return bookingService.readAll(userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long bookingId,
                          @RequestBody BookingDto bookingDto) {
        log.debug("{} update({})", this.getClass().getName(), bookingId);
        return bookingService.update(userId, bookingId, bookingDto);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long bookingId) {
        log.debug("{} delete({})", this.getClass().getName(), bookingId);
        bookingService.delete(userId, bookingId);
    }

}
