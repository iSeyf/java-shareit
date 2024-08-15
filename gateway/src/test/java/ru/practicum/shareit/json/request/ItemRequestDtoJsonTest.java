package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;
    @Autowired
    private JacksonTester<InputItemRequestDto> inputJson;


    @Test
    void testSerialize() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1, "Description", 1, LocalDateTime.of(2024, 8, 13, 16, 58, 21), new ArrayList<>());

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).hasJsonPathNumberValue("$.requestor");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-08-13T16:58:21");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"Description\"}";

        InputItemRequestDto requestDto = inputJson.parseObject(content);

        assertThat(requestDto.getDescription()).isEqualTo("Description");
    }
}
