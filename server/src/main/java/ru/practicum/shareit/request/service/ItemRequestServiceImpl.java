package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(InputItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));

        List<Long> requestIds = itemRequestDtoList.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllItemByRequestIdIn(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())));

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            itemRequestDto.setItems(itemsByRequestId.getOrDefault(itemRequestDto.getId(), Collections.emptyList()));
        }

        return itemRequestDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        Page<ItemRequest> page = requestRepository.findAllByOrderByCreatedDesc(PageRequest.of(from, size));

        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(page.getContent());
        List<Long> requestIds = itemRequestDtoList.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllItemByRequestIdIn(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())));

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            List<ItemDto> itemsDto = itemsByRequestId.getOrDefault(itemRequestDto.getId(), new ArrayList<>());
            itemRequestDto.setItems(itemsDto);
        }

        return itemRequestDtoList;

    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с таким ID не найден."));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> itemDtoList = ItemMapper.toItemDtoList(itemRepository.findAllItemByRequestId(requestId));
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }
}
