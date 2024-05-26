package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int request;

    public ItemDto(String name, String description, boolean available, int request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
