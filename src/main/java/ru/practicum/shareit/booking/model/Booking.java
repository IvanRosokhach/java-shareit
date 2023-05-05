package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Booking {

    private long id; //id — уникальный идентификатор бронирования;
    @FutureOrPresent
    private LocalDateTime start; // — дата и время начала бронирования;
    @FutureOrPresent
    private LocalDateTime end; // — дата и время конца бронирования;
    @NotNull
    private Item item; // — вещь, которую пользователь бронирует;
    @NotNull
    private User booker; // — пользователь, который осуществляет бронирование;
    @NotNull
    private BookingStatus status; // — статус бронирования
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.

}
