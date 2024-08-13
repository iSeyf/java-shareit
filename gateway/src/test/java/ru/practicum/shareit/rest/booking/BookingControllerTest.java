package ru.practicum.shareit.rest.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


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

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient client;

    @InjectMocks
    private BookingController bookingController;

    @Test
    public void addBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(100), new ItemDto(), new UserDto(), BookingStatus.WAITING);
        when(client.addBooking(anyLong(), any(BookingRequestDto.class))).thenReturn(ResponseEntity.ok(bookingDto));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void respondBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(100), new ItemDto(), new UserDto(), BookingStatus.WAITING);
        when(client.respondBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(bookingDto));
        bookingDto.setStatus(BookingStatus.APPROVED);
        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(100), new ItemDto(), new UserDto(), BookingStatus.WAITING);
        when(client.getBookingById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(bookingDto));
        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getAllBookingsByUserTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 8, 12, 16, 58, 21);
        LocalDateTime end = LocalDateTime.of(2024, 8, 13, 16, 58, 21);
        UserDto user = new UserDto(1L, "Name", "email@mail.ru");
        ItemDto item = new ItemDto(1L, "Name", "Description", true, null, null, null, null);
        BookingDto bookingDto = new BookingDto(1, start, end, item, user, BookingStatus.WAITING);
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);
        when(client.getBookingsByUser(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(bookings));
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(bookings.size())))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is("2024-08-12T16:58:21")))
                .andExpect(jsonPath("$.[0].end", is("2024-08-13T16:58:21")))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(item.getName())));
    }

    @Test
    public void getAllBookingByOwnerTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 8, 12, 16, 58, 21);
        LocalDateTime end = LocalDateTime.of(2024, 8, 13, 16, 58, 21);
        UserDto user = new UserDto(1L, "Name", "email@mail.ru");
        ItemDto item = new ItemDto(1L, "Name", "Description", true, null, null, null, null);
        BookingDto bookingDto = new BookingDto(1, start, end, item, user, BookingStatus.WAITING);
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);
        when(client.getBookingsByOwner(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(bookings));
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(bookings.size())))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is("2024-08-12T16:58:21")))
                .andExpect(jsonPath("$.[0].end", is("2024-08-13T16:58:21")))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(item.getName())));
    }
}
