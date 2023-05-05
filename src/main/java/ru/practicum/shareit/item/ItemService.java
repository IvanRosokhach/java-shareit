package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item create(long userId, ItemDto itemDto);

    ItemDto read(long userId, long itemId);

    Collection<ItemDto> readAll(long userId);

    Item update(long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    Collection<Item> search(long userId, String text);

    Comment createComment(long userId, long itemId, CommentDto commentDto);

}
