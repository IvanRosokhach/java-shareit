package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Validated ItemDto itemDto) {
        log.debug("Create");
        return ItemMapper.toItemDto(itemService.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable long itemId) {
        log.debug("Read");
        return itemService.read(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("ReadAll");
        return itemService.readAll(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Update({})", itemId);
        return ItemMapper.toItemDto(itemService.update(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId) {
        log.debug("Delete({})", itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam String text) {
        log.debug("Search({})", text);
        return itemService.search(userId, text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Validated CommentDto commentDto) {
        log.debug("{}/CreateComment()", itemId);
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, commentDto));
    }

}
