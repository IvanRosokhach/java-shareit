package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Validated ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item read(@RequestHeader("X-Sharer-User-Id") Long userId,
                     @PathVariable long itemId) {
        return itemService.read(userId,itemId);
    }

    @GetMapping
    public Collection<Item> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.readAll(userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId,
                       @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/search")  //TODO
    public List<Item> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam String text) {
        return itemService.search(text);
    }

}
