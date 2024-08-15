package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;
    @Autowired
    private JacksonTester<BookingRequestDto> requestJson;

    @Test
    void testSerialize() throws Exception {
        ItemDto item = new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null);
        UserDto user = new UserDto(1L, "User Name", "user@example.com");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2024, 8, 12, 16, 58, 21),
                LocalDateTime.of(2024, 8, 13, 16, 58, 21),
                item, user, BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-08-12T16:58:21");
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-08-13T16:58:21");
        assertThat(result).hasJsonPathStringValue("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2024-08-12T16:58:21\",\"end\":\"2024-08-13T16:58:21\"}";

        BookingRequestDto bookingDto = requestJson.parseObject(content);

        assertThat(bookingDto.getItemId()).isEqualTo(1);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 8, 12, 16, 58, 21));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 8, 13, 16, 58, 21));
    }
}
