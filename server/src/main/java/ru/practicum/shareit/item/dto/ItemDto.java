package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;

@Data
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;

    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Collection<CommentDto> comments;
    private Long requestId;

}
