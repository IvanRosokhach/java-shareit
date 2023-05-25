package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping()
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Validated ItemRequestDto itemRequestDto) {
        log.debug("Create");
        return requestService.create(userId, itemRequestDto);
    }

    @GetMapping()
    public Collection<ItemRequestDto> read(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Read");
        return requestService.read(userId);
    }

    @GetMapping("{itemId}")
    public ItemRequestDto read(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.debug("Read({})", itemId);
        return requestService.read(userId, itemId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> readAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("ReadAll");
        return requestService.readAll(userId, from, size);
    }

}
