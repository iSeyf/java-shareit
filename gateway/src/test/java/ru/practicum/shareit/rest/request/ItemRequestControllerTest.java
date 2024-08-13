package ru.practicum.shareit.rest.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient client;

    @InjectMocks
    private ItemRequestController controller;

    @Test
    public void addItemRequestTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1, "Description", 1, LocalDateTime.now(), new ArrayList<>());

        when(client.addItemRequest(anyLong(), any(InputItemRequestDto.class))).thenReturn(ResponseEntity.ok(requestDto));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(requestDto.getRequestor()), Long.class));
    }

    @Test
    public void getUserRequestsTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1, "Description", 1, LocalDateTime.now(), new ArrayList<>());
        ItemRequestDto requestDto2 = new ItemRequestDto(2, "Description2", 1, LocalDateTime.now(), new ArrayList<>());
        List<ItemRequestDto> requests = new ArrayList<>();
        requests.add(requestDto);
        requests.add(requestDto2);

        when(client.getUserRequests(anyLong())).thenReturn(ResponseEntity.ok(requests));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(requests))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(requests.size())))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(requestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$.[1].requestor", is(requestDto2.getRequestor()), Long.class));
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1, "Description", 1, LocalDateTime.now(), new ArrayList<>());
        ItemRequestDto requestDto2 = new ItemRequestDto(2, "Description2", 1, LocalDateTime.now(), new ArrayList<>());
        List<ItemRequestDto> requests = new ArrayList<>();
        requests.add(requestDto);
        requests.add(requestDto2);

        when(client.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(requests));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(requests))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(requests.size())))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(requestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$.[1].requestor", is(requestDto2.getRequestor()), Long.class));
    }

    @Test
    public void getRequestByIdTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1, "Description", 1, LocalDateTime.now(), new ArrayList<>());
        when(client.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(requestDto));

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(requestDto.getRequestor()), Long.class));
    }
}
