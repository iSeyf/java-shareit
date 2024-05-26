package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {
    private int itemId = 0;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new ValidationException("Item name cannot be empty.");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty.");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Availability must be specified.");
        }
        itemDto.setId(createItemId());
        items.put(itemDto.getId(), ItemMapper.toItem(itemDto, ownerId));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Предмет не найден!");
        }
        if (item.getOwner() != ownerId) {
            throw new NotFoundException("Предмет не найден!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.remove(id);
        items.put(id, item);
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public List<ItemDto> getItems(int ownerId) {
        List<Item> itemList = new ArrayList<>(items.values());
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getOwner() == ownerId) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemList = new ArrayList<>();
        if (!text.isEmpty()) {
            String searchedItem = text.trim().toLowerCase();
            for (Item item : items.values()) {
                String itemName = item.getName().trim().toLowerCase();
                String itemDescription = item.getDescription().trim().toLowerCase();
                if (item.isAvailable()) {
                    if (itemName.contains(searchedItem) || itemDescription.contains(searchedItem)) {
                        itemList.add(ItemMapper.toItemDto(item));
                    }
                }
            }
        }
        return itemList;
    }

    private int createItemId() {
        return ++itemId;
    }
}
