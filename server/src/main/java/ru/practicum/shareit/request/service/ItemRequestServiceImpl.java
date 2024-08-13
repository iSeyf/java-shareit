package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(InputItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            List<Item> items = itemRepository.findAllItemByRequestId(itemRequestDto.getId());
            List<ItemDto> itemsDto = ItemMapper.toItemDtoList(items);
            itemRequestDto.setItems(itemsDto);
        }
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        Page<ItemRequest> page = requestRepository.findAllByOrderByCreatedDesc(PageRequest.of(from, size));
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(page.getContent());

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            List<Item> items = itemRepository.findAllItemByRequestId(itemRequestDto.getId());
            List<ItemDto> itemsDto = ItemMapper.toItemDtoList(items);
            itemRequestDto.setItems(itemsDto);
        }

        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с таким ID не найден."));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> itemDtoList = ItemMapper.toItemDtoList(itemRepository.findAllItemByRequestId(requestId));
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }
}
