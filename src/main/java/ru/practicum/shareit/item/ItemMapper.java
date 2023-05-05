package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static ShortItemDto toShortItem(Item item) {
        ShortItemDto itemDto = new ShortItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto toFullItemDto(Item item, Collection<Booking> bookings, Collection<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        if (bookings != null && !bookings.isEmpty()) {
            LocalDateTime time = LocalDateTime.now();

            Optional<Booking> last = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(time))
                    .max(Comparator.comparing(Booking::getEnd));

            if (last.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toShortBookingDto(last.get()));

                Optional<Booking> next = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(time))
                        .min(Comparator.comparing(Booking::getStart));

                if (next.isPresent()) {
                    itemDto.setNextBooking(BookingMapper.toShortBookingDto(next.get()));
                }
            }
        }
        itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

}
