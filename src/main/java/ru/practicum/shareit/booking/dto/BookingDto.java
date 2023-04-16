package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    private Long item;
    @NotNull
    private Long booker;
    @NotNull
    @Pattern(regexp = "^WAITING$|^APPROVED$|^REJECTED$|^CANCELED$",
            message = "allowed input: WAITING/APPROVED/REJECTED/CANCELED")
    private String status;
}
