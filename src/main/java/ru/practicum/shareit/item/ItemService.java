package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item create(long userId, ItemDto itemDto);

    Item read(long userId, long id);

    Collection<Item> readAll(long userId);

    Item update(long userId, long itemId, ItemDto itemDto);

    void delete(long id);

    List<Item> search(String text);

}
