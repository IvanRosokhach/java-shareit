package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid RequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> read(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests, userId={}", userId);
        return requestClient.read(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> read(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.read(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> readAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all requests, userId={}", userId);
        return requestClient.readAll(userId, from, size);
    }

}
