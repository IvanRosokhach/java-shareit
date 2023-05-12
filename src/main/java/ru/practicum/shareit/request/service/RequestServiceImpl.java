package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM_REQUEST;
import static ru.practicum.shareit.exception.Constant.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId))));
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest saved = requestRepository.save(itemRequest);
        log.debug("Запрос вещи создан.");
        return RequestMapper.toRequestDto(saved);
    }

    @Override
    public List<ItemRequestDto> read(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        Map<Long, ItemRequest> collectItemRequest = requestRepository.findAllByRequestorId(userId)
                .stream().collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<Item>> collectItem = itemRepository.findAllByRequestRequestorId(userId)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        List<ItemRequestDto> itemRequestDtos = collectItemRequest.values().stream()
                .map(itemRequest -> RequestMapper.toRequestDto(itemRequest,
                        collectItem.getOrDefault(itemRequest.getRequestor().getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        log.debug("Найдено запросов вещей: {}.", itemRequestDtos.size());
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto read(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM_REQUEST, requestId)));

        Collection<Item> allByRequestId = itemRepository.findAllByRequestId(itemRequest.getId());
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest, allByRequestId);
        log.debug("Запрос вещи с id: {} найден.", requestId);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> readAll(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, Sort.by("created").ascending());
        List<ItemRequestDto> content = requestRepository.findAllByRequestorIdIsNot(userId, page)
                .map(RequestMapper::toRequestDto).stream()
                .peek(itemRequestDto -> itemRequestDto.setItems(
                        itemRepository.findAllByRequestId(itemRequestDto.getId())
                                .stream().map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        log.debug("Всего запросов вещей: {}.", content.size());
        return content;
    }

}
