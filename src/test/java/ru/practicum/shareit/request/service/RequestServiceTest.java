package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Captor
    ArgumentCaptor<ItemRequest> argumentCaptor;

    private User user;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        user = User.builder()
                .id(2L)
                .name("user")
                .email("user@user.ru")
                .build();

        request = ItemRequest.builder()
                .id(4L)
                .description("desc")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(5L)
                .name("itemName")
                .description("itemDesc")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
    }

    @Test
    void create_whenInvoke_thenReturnItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("desc");
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(requestRepository.save(any())).thenReturn(request);

        requestService.create(user.getId(), itemRequestDto);

        verify(requestRepository).save(argumentCaptor.capture());
        ItemRequest actual = argumentCaptor.getValue();

        verify(requestRepository).save(any());
        assertEquals(itemRequestDto.getDescription(), actual.getDescription());
        assertEquals(user.getId(), actual.getRequestor().getId());
        assertNotNull(actual.getCreated());
    }

    @Test
    void read_whenInvoke_thenReturnItemRequests() {
        when(requestRepository.findAllByRequestorId(user.getId())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestRequestorId(user.getId())).thenReturn(List.of(item));

        List<ItemRequestDto> actual = requestService.read(user.getId());

        assertEquals(request.getId(), actual.get(0).getId());
        assertEquals(request.getDescription(), actual.get(0).getDescription());
        assertNotNull(actual.get(0).getCreated());
    }

    @Test
    void read_whenInvoke_thenReturnItemRequest() {
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(List.of(item));

        ItemRequestDto actual = requestService.read(user.getId(), request.getId());

        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getDescription(), actual.getDescription());
        assertEquals(1, actual.getItems().size());
    }

    @Test
    void readAll_whenInvoke_thenReturnItemRequests() {
        when(requestRepository.findAllByRequestorIdIsNot(any(), any())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(List.of(item));

        List<ItemRequestDto> actual = requestService.readAll(user.getId(), 0, 10);

        assertEquals(request.getId(), actual.get(0).getId());
        assertEquals(request.getDescription(), actual.get(0).getDescription());
        assertEquals(1, actual.get(0).getItems().size());
    }

    @Test
    void getItemRequestById_whenInvoke_thenReturnItemRequest() {
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequest result = requestService.getItemRequestById(request.getId());

        assertEquals(request, result);
    }

    @Test
    void getItemRequestById_whenRequestNotExist_thenNotFoundExceptionThrow() {
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(request.getId()));
    }

}