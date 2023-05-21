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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(userService.getUserById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest saved = requestRepository.save(itemRequest);
        log.debug("Запрос вещи создан.");
        return RequestMapper.toRequestDto(saved);
    }

    @Override
    public List<ItemRequestDto> read(Long userId) {
        userService.userIsExist(userId);
        Map<Long, ItemRequest> collectItemRequest = requestRepository.findAllByRequestorId(userId)
                .stream().collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<Item>> collectItem = itemRepository.findAllByRequestRequestorId(userId)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        List<ItemRequestDto> itemRequestDto = collectItemRequest.values().stream()
                .map(itemRequest -> RequestMapper.toRequestDto(itemRequest,
                        collectItem.getOrDefault(itemRequest.getRequestor().getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        log.debug("Найдено запросов вещей: {}.", itemRequestDto.size());
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto read(Long userId, Long requestId) {
        userService.userIsExist(userId);
        ItemRequest itemRequest = getItemRequestById(requestId);
        Collection<Item> allByRequestId = itemRepository.findAllByRequestId(itemRequest.getId());
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest, allByRequestId);
        log.debug("Запрос вещи с id: {} найден.", requestId);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> readAll(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, Sort.by("created").ascending());
        List<ItemRequestDto> content = requestRepository.findAllByRequestorIdIsNot(userId, page).stream()
                .map(RequestMapper::toRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(
                        itemRepository.findAllByRequestId(itemRequestDto.getId())
                                .stream().map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        log.debug("Всего запросов вещей: {}.", content.size());
        return content;
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM_REQUEST, requestId)));
    }

}
