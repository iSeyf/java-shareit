package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Autowired
    private JacksonTester<InputCommentDto> inputJson;

    @Test
    void testSerialize() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Test comment", 2L, "Author", LocalDateTime.of(2024, 8, 12, 16, 58, 21, 417921300));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).hasJsonPathStringValue("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-08-12T16:58:21.4179213");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"text\":\"Test comment\"}";

        InputCommentDto commentDto = inputJson.parseObject(content);

        assertThat(commentDto.getText()).isEqualTo("Test comment");
    }
}
