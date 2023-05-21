package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto read(long userId, long itemId);

    Collection<ItemDto> readAll(long userId, int from, int size);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    Collection<ItemDto> search(long userId, String text, int from, int size);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

    Item getItemById(long itemId);

}
