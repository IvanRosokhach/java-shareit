package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDateTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.exception.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new IncorrectDateTimeException();
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Не доступна для бронирования.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId)));
        if (user.getId() == item.getOwner().getId()) {
            throw new NotFoundException("Пользователь является владельцем вещи.");
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        log.debug("Бронирование создано с id: {}.", saved.getId());
        return BookingMapper.toBookingDto(saved);
    }

    @Override
    public BookingDto read(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            log.debug("Бронирование с id: {} найдено.", bookingId);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotOwnerException();
        }
    }

    @Override
    public Collection<BookingDto> readAll(long userId, BookingState state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        final LocalDateTime time = LocalDateTime.now();
        Collection<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, time, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, time, page);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.toString()), page);
                break;
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }
        log.debug("Всего бронирований: {}.", bookings.size());
        return BookingMapper.toBookingDto(bookings);
    }

    public BookingDto updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotOwnerException();
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException("Статус уже установлен.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        log.debug("Статус обновлен: {}", booking.getStatus());
        return BookingMapper.toBookingDto(saved);
    }

    @Override
    public void delete(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getBooker().getId() != userId) {
            throw new NotOwnerException();
        } else {
            bookingRepository.deleteById(bookingId);
            log.debug("Бронирование с id: {} удалено", bookingId);
        }
    }

    public Collection<BookingDto> readForOwner(long userId, BookingState state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        Collection<Booking> bookings;
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllForOwnerPast(userId, time, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllForOwnerCurrent(userId, time, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllForOwnerFuture(userId, time, page);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllForOwnerState(BookingStatus.valueOf(state.toString()), userId, page);
                break;
            default:
                bookings = bookingRepository.findAllForOwner(userId, page);
                break;
        }
        log.debug("Всего бронирований: {} для владельца вещи с id {}.", bookings.size(), userId);
        return BookingMapper.toBookingDto(bookings);
    }

}
