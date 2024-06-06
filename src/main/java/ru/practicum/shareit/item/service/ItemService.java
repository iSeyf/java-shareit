package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int id, ItemDto itemDto, int ownerId);

    ItemDto getItemById(int id);

    List<ItemDto> getItems(int ownerId);

    List<ItemDto> searchItem(String text);
}
