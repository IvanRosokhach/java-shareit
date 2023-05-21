package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private final long userId = 1L;
    private final long itemId = 1L;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("desc");
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenReturnOk() {
        when(requestService.create(userId, itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
        verify(requestService).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void readUserId_whenInvoke_thenReturnOk() {
        List<ItemRequestDto> requests = List.of(itemRequestDto);
        when(requestService.read(userId)).thenReturn(requests);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requests), result);
        verify(requestService).read(userId);
    }

    @SneakyThrows
    @Test
    void readItemId_whenInvoke_thenReturnOk() {
        when(requestService.read(userId, itemId)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
        verify(requestService).read(userId, itemId);
    }

    @SneakyThrows
    @Test
    void readAll_whenInvoke_thenReturnOk() {
        List<ItemRequestDto> requests = List.of(itemRequestDto);
        when(requestService.readAll(userId, 0, 10)).thenReturn(requests);

        String result = mockMvc.perform(get("/requests//all")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requests), result);
        verify(requestService).readAll(userId, 0, 10);
    }

}