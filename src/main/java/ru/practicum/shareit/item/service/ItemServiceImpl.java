package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage) {
        this.itemStorage = storage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        userStorage.getUserById(ownerId);
        return itemStorage.addItem(itemDto, ownerId);
    }

    @Override
    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId) {
        ItemDto updatedItem = getItemById(id);
        if (updatedItem == null) {
            throw new NotFoundException("Предмет не найден!");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return itemStorage.updateItem(id, updatedItem, ownerId);
    }

    @Override
    public ItemDto getItemById(int id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public List<ItemDto> getItems(int ownerId) {
        return itemStorage.getItems(ownerId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
