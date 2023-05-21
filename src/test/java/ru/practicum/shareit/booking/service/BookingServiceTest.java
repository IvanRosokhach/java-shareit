package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    ArgumentCaptor<Booking> argumentCaptor;

    private User owner;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingToSave;
    private LocalDateTime time;
    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        user = User.builder()
                .id(3L)
                .name("user")
                .email("user@user.ru")
                .build();

        item = Item.builder()
                .id(4L)
                .name("itemName")
                .description("itemDesc")
                .available(true)
                .owner(owner)
                .build();

        time = LocalDateTime.now();

        booking = Booking.builder()
                .start(time.minusHours(1))
                .end(time.minusMinutes(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        bookingToSave = BookingDto.builder()
                .start(time.plusMinutes(1))
                .end(time.plusHours(1))
                .itemId(item.getId())
                .build();
    }

    @Test
    void create_whenInvoke_thenSavedItem() {
        when(itemService.getItemById(item.getId())).thenReturn(item);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.create(user.getId(), bookingToSave);

        verify(bookingRepository).save(argumentCaptor.capture());
        Booking actual = argumentCaptor.getValue();

        verify(bookingRepository).save(any());
        assertEquals(bookingToSave.getStart(), actual.getStart());
        assertEquals(bookingToSave.getEnd(), actual.getEnd());
        assertEquals(bookingToSave.getItemId(), actual.getItem().getId());
        assertEquals(user.getId(), actual.getBooker().getId());
    }


    @Test
    void create_whenNotValidTime_thenIncorrectDateTimeExceptionThrow() {
        bookingToSave.setStart(time.plusHours(2));
        bookingToSave.setEnd(time.plusHours(1));

        assertThrows(IncorrectDateTimeException.class, () -> bookingService.create(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable_thenNotAvailable() {
        item.setAvailable(false);
        when(itemService.getItemById(item.getId())).thenReturn(item);

        assertThrows(NotAvailableException.class, () -> bookingService.create(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookerIsTheItemOwner_thenNotFoundExceptionThrow() {
        when(itemService.getItemById(item.getId())).thenReturn(item);
        when(userService.getUserById(owner.getId())).thenReturn(owner);

        assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void read_whenInvoke_thenReturnBooking() {
        BookingDto expected = BookingMapper.toBookingDto(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.read(owner.getId(), booking.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void read_whenUserNotOwnerOrNotBooker_thenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class, () -> bookingService.read(999L, booking.getId()));
    }

    @Test
    void readAll_whenStateAll_thenReturnBookings() {
        when(bookingRepository.findAllByBookerId(any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.ALL, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readAll_whenStatePast_thenReturnBookings() {
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.PAST, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readAll_whenStateCurrent_thenReturnBookings() {
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                any(), any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.CURRENT, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readAll_whenStateFuture_thenReturnBookings() {
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.FUTURE, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readAll_whenStateWaiting_thenReturnBookings() {
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.WAITING, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readAll_whenStateRejected_thenReturnBookings() {
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readAll(owner.getId(), BookingState.REJECTED, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void updateStatus_whenInvoke_thenReturnUpdatedBooking() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.updateStatus(owner.getId(), booking.getId(), true);

        verify(bookingRepository).save(booking);
    }

    @Test
    void updateStatus_whenStatusIsAlreadyApprove_thenNotAvailableExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class,
                () -> bookingService.updateStatus(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateStatus_whenUserNotExist_thenNotFoundExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateStatus_whenUserWasNotOwner_thenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class,
                () -> bookingService.updateStatus(user.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void delete_whenUserWasNotOwner_thenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class,
                () -> bookingService.delete(owner.getId(), booking.getId()));

        verify(bookingRepository, never()).deleteById(booking.getId());
    }

    @Test
    void delete_whenInvoke_thenDeleteBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.delete(user.getId(), booking.getId());

        verify(bookingRepository).deleteById(booking.getId());
    }


    @Test
    void readForOwner_whenStateAll_thenReturnBookings() {
        when(bookingRepository.findAllForOwner(any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.ALL, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readForOwner_whenStatePast_thenReturnBookings() {
        when(bookingRepository.findAllForOwnerPast(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.PAST, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readForOwner_whenStateCurrent_thenReturnBookings() {
        when(bookingRepository.findAllForOwnerCurrent(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.CURRENT, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readForOwner_whenStateFuture_thenReturnBookings() {
        when(bookingRepository.findAllForOwnerFuture(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.FUTURE, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readForOwner_whenStateWaiting_thenReturnBookings() {
        when(bookingRepository.findAllForOwnerState(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.WAITING, from, size);

        assertEquals(1, actual.size());
    }

    @Test
    void readForOwner_whenStateRejected_thenReturnBookings() {
        when(bookingRepository.findAllForOwnerState(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actual = bookingService.readForOwner(owner.getId(), BookingState.REJECTED, from, size);

        assertEquals(1, actual.size());
    }

}