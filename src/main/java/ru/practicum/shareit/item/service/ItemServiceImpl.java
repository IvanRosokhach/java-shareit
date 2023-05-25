package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UncompletedBookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM;
import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getUserById(userId));
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format(NOT_FOUND_ITEM_REQUEST, itemDto.getRequestId()))));
        }
        Item savedItem = itemRepository.save(item);
        log.debug("Вещь добавлена с id: {}.", savedItem.getId());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto read(long userId, long itemId) {
        Item item = getItemById(itemId);
        Collection<Booking> itemBookings;
        if (item.getOwner().getId() == userId) {
            itemBookings = new ArrayList<>(bookingRepository.findAllByItemIdIn(Set.of(item.getId())));
        } else {
            itemBookings = Collections.emptyList();
        }
        Collection<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        ItemDto itemDto = ItemMapper.toFullItemDto(item, itemBookings, itemComments);
        log.debug("Вещь с id: {} найдена.", itemId);
        return itemDto;
    }

    @Override
    public Collection<ItemDto> readAll(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Map<Long, Item> itemsByOwner = itemRepository.findAllByOwnerId(userId, page)
                .stream().collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Booking>> bookingsByItems = bookingRepository.findAllByItemIdIn(itemsByOwner.keySet())
                .stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsByItems = commentRepository.findAllByItemIdIn(itemsByOwner.keySet())
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemDto> collect = itemsByOwner.values().stream()
                .map(item -> ItemMapper.toFullItemDto(item,
                        bookingsByItems.getOrDefault(item.getId(), Collections.emptyList()),
                        commentsByItems.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        log.debug("Всего вещей: {} пользователя с id: {}.", collect.size(), userId);
        return collect;
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        userService.userIsExist(userId);
        Item updatedItem = getItemById(itemId);
        if (updatedItem.getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не является владельцем вещи.");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        Item saved = itemRepository.save(updatedItem);
        log.debug("Вещь с id: {} обновлена.", itemId);
        return ItemMapper.toItemDto(saved);

    }

    @Override
    public void delete(long userId, long itemId) {
        userService.userIsExist(userId);
        Item item = getItemById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не является владельцем вещи.");
        }
        itemRepository.deleteById(itemId);
        log.debug("Вещь с id: {} удалена.", itemId);
    }

    @Override
    public Collection<ItemDto> search(long userId, String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }
        PageRequest page = PageRequest.of(from / size, size);
        Collection<Item> searched = itemRepository.search(text, page);
        log.debug("Вещей найден: {}.", searched.size());
        return ItemMapper.toItemDto(searched);
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        LocalDateTime time = LocalDateTime.now();
        if (bookingRepository
                .findAllByBookerIdAndItemIdAndEndIsBefore(userId, itemId, time)
                .isEmpty()) {
            throw new UncompletedBookingException("Нельзя создать отзыв для незавершенного бронирования.");
        }
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(time);
        Comment savedComment = commentRepository.save(comment);
        log.debug("Комментарий с id: {} сохранен.", savedComment.getId());
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, itemId)));
    }

    @Override
    public Collection<Item> findAllByRequestRequestorId(long userId) {
        return itemRepository.findAllByRequestRequestorId(userId);
    }

    @Override
    public Collection<Item> findAllByRequestId(long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }

}
