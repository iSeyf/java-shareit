package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(long id, ItemDto itemDto, long ownerId);

    ItemDto getItemById(long id, long ownerId);

    List<ItemDto> getItems(long ownerId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(long itemId, InputCommentDto commentDto, long userId);
}
