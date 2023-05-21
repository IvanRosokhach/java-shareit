package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final long userId = 1L;
    private final long bookingId = 1L;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setItemId(1L);
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenReturnOk() {
        when(bookingService.create(userId, bookingDto)).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).create(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void create_whenDtoNotValid_thenReturnBadRequest() {
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void read_whenInvoke_thenReturnOk() {
        when(bookingService.read(userId, bookingId)).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).read(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void readAll_whenInvoke_thenReturnOk() {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.readAll(userId, BookingState.ALL, 0, 10)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
        verify(bookingService).readAll(userId, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void update_whenInvoke_thenReturnOk() {
        when(bookingService.updateStatus(userId, bookingId, true)).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).updateStatus(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenReturnOk() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).delete(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void readForOwner_whenInvoke_thenReturnOk() {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.readForOwner(userId, BookingState.ALL, 0, 10)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
        verify(bookingService).readForOwner(userId, BookingState.ALL, 0, 10);
    }

}