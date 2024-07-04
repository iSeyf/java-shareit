package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(long id, String text, long itemId, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorName = authorName;
        this.created = created;
    }
}
