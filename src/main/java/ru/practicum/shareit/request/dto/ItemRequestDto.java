package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class ItemRequestDto {

    long id;
    @NotBlank
    String description;
    LocalDateTime created;
    Collection<ItemDto> items;

}
