/*package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
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
    private Map<Integer, List<Item>> ownerItems = new HashMap<>();

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto, ownerId);
        item.setId(createItemId());
        items.put(item.getId(), item);

        ownerItems.computeIfAbsent(ownerId, k -> new ArrayList<>());
        ownerItems.get(ownerId).add(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId) {
        Item item = items.get(id);
        if (item.getOwner() != ownerId) {
            throw new NotFoundException("Предмет не найден.");
        }
        ownerItems.get(ownerId).remove(item);

        Item updatedItem = ItemMapper.toItem(itemDto, ownerId);
        items.put(id, updatedItem);
        ownerItems.get(ownerId).add(updatedItem);

        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public ItemDto getItemById(int id) {
        if (items.get(id) == null) {
            throw new NotFoundException("Предмет не найден.");
        }
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public List<ItemDto> getItems(int ownerId) {
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : ownerItems.get(ownerId)) {
            userItems.add(ItemMapper.toItemDto(item));
        }
        return userItems;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemList = new ArrayList<>();
        if (!text.isEmpty()) {
            String searchedItem = text.trim().toLowerCase();
            for (Item item : items.values()) {
                String itemName = item.getName().toLowerCase();
                String itemDescription = item.getDescription().toLowerCase();
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
*/