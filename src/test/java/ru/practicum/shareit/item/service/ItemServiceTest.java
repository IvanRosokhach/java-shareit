package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    ArgumentCaptor<Item> argumentCaptor;

    private final long id = 1L;

    private UserDto userDto;
    private ItemDto itemDto;
    private CommentDto commentDto;

    private User owner;
    private User user;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest request;


    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");

        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        user = User.builder()
                .id(3L)
                .name("user")
                .email("user@user.ru")
                .build();

        item = Item.builder()
                .id(4L)
                .name("itemName")
                .description("itemDesc")
                .available(true)
                .owner(owner)
                .build();

        itemDto = new ItemDto();
        itemDto.setName("itemName");
        itemDto.setDescription("itemDesc");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("comment");

        LocalDateTime time = LocalDateTime.now();

        booking = Booking.builder()
                .start(time.minusHours(1))
                .end(time.minusMinutes(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        comment = Comment.builder()
                .id(5L)
                .text("comment")
                .created(time)
                .author(user)
                .item(item)
                .build();

        request = ItemRequest.builder()
                .id(6L)
                .description("desc")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createWhenInvokeThenSavedItem() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actual = itemService.create(user.getId(), itemDto);

        verify(itemRepository).save(any());
        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertEquals(item.getAvailable(), actual.getAvailable());
    }

    @Test
    void createWhenInvokeThenSavedItemAndSetOwner() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);

        itemService.create(user.getId(), itemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item actual = argumentCaptor.getValue();
        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertEquals(item.getAvailable(), actual.getAvailable());
        assertEquals(user.getId(), actual.getOwner().getId());
    }

    @Test
    void createWhenInvokeThenSavedItemAndSetRequest() {
        User user = UserMapper.toUser(userDto);
        itemDto.setRequestId(6L);
        Item item = ItemMapper.toItem(itemDto);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(requestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        itemService.create(user.getId(), itemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item actual = argumentCaptor.getValue();
        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertEquals(item.getAvailable(), actual.getAvailable());
        assertEquals(user.getId(), actual.getOwner().getId());
        assertEquals(itemDto.getRequestId(), actual.getRequest().getId());
    }

    @Test
    void createWhenUserNotFoundThenNotFoundExceptionThrow() {
        User user = UserMapper.toUser(userDto);
        when(userService.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(user.getId(), itemDto));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void readWhenLastBookingExistThenReturnItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdIn(Set.of(item.getId()))).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemDto actual = itemService.read(owner.getId(), item.getId());

        assertEquals(booking.getBooker().getId(), actual.getLastBooking().getBookerId());
        assertNull(actual.getNextBooking());
        assertEquals(1, actual.getComments().size());
    }

    @Test
    void readWhenBookingsNotExistThenReturnItemWithoutBookings() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemDto actual = itemService.read(user.getId(), item.getId());

        assertNull(actual.getLastBooking());
        assertNull(actual.getNextBooking());
        assertEquals(1, actual.getComments().size());
    }

    @Test
    void readAllWhenInvokeThenReturnItems() {
        when(itemRepository.findAllByOwnerId(owner.getId(), PageRequest.of(0, 10))).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(Set.of(item.getId()))).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdIn(Set.of(item.getId()))).thenReturn(List.of(comment));

        List<ItemDto> actual = (List<ItemDto>) itemService.readAll(owner.getId(), 0, 10);

        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getLastBooking().getBookerId());
        assertNull(actual.get(0).getNextBooking());
        Assertions.assertEquals(1, actual.get(0).getComments().size());
        assertEquals(1, actual.size());
    }

    @Test
    void updateWhenInvokeThenReturnItemWithUpdatedFields() {
        Item oldItem = ItemMapper.toItem(itemDto);
        oldItem.setId(1L);
        oldItem.setOwner(owner);
        ItemDto newItem = new ItemDto();
        newItem.setName("nameUpdated");
        newItem.setDescription("updatedDesc");
        newItem.setAvailable(false);
        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(oldItem)).thenReturn(oldItem);

        ItemDto updated = itemService.update(owner.getId(), oldItem.getId(), newItem);

        verify(itemRepository).save(any());
        assertEquals(newItem.getName(), updated.getName());
        assertEquals(newItem.getDescription(), updated.getDescription());
        assertEquals(newItem.getAvailable(), updated.getAvailable());
    }

    @Test
    void updateWhenInvokeThenReturnItemWithUnUpdatedFields() {
        Item oldItem = ItemMapper.toItem(itemDto);
        oldItem.setId(1L);
        oldItem.setOwner(owner);
        ItemDto newItem = new ItemDto();
        newItem.setName(null);
        newItem.setDescription(null);
        newItem.setAvailable(null);
        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(oldItem)).thenReturn(oldItem);

        ItemDto updated = itemService.update(owner.getId(), oldItem.getId(), newItem);

        verify(itemRepository).save(any());
        assertEquals(oldItem.getName(), updated.getName());
        assertEquals(oldItem.getDescription(), updated.getDescription());
        assertEquals(oldItem.getAvailable(), updated.getAvailable());
    }

    @Test
    void updateWhenInvokeNotItemOwnerThenNotOwnerExceptionThrow() {
        Item oldItem = ItemMapper.toItem(itemDto);
        oldItem.setId(1L);
        oldItem.setOwner(owner);
        ItemDto newItem = new ItemDto();
        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));

        assertThrows(NotOwnerException.class, () -> itemService.update(user.getId(), oldItem.getId(), newItem));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void deleteWhenInvokeThenDeleteItem() {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        itemService.delete(owner.getId(), id);

        verify(itemRepository).deleteById(id);
    }

    @Test
    void deleteWhenInvokeNotOwnerThenNotOwnerExceptionThrow() {
        long notOwnerId = 1L;
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        assertThrows(NotOwnerException.class, () -> itemService.delete(notOwnerId, id));

        verify(itemRepository, never()).deleteById(id);
    }

    @Test
    void searchWhenEmptySearchTextThenReturnEmptyResult() {
        String text = "";
        Collection<ItemDto> result = itemService.search(id, text, 0, 10);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(any(), any());

    }

    @Test
    void searchWhenBlankSearchTextThenReturnEmptyResult() {
        String text = "              ";
        Collection<ItemDto> result = itemService.search(id, text, 0, 10);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(any(), any());
    }

    @Test
    void searchWhenInvokeThenReturnResult() {
        int from = 0;
        int size = 10;
        when(itemRepository.search("item", PageRequest.of(from, size))).thenReturn(List.of(ItemMapper.toItem(itemDto)));

        Collection<ItemDto> result = itemService.search(id, "item", from, size);

        verify(itemRepository).search("item", PageRequest.of(from, size));
        assertIterableEquals(List.of(itemDto), result);
    }

    @Test
    void createCommentWhenInvokeThenReturnSavedComment() {
        List<Booking> notEmpty = List.of(new Booking());
        when(userService.getUserById(id)).thenReturn(user);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndIsBefore(any(), any(), any())).thenReturn(notEmpty);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(id, id, commentDto);

        verify(commentRepository).save(any());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
    }

    @Test
    void createCommentWhenWasNoBookingThenUncompletedBookingExceptionThrow() {
        List<Booking> empty = List.of();
        when(userService.getUserById(id)).thenReturn(owner);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndIsBefore(any(), any(), any())).thenReturn(empty);

        assertThrows(UncompletedBookingException.class, () -> itemService.createComment(id, id, commentDto));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void getItemByIdWhenItemNotExistThenNotFoundExceptionThrow() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(id));
    }

}