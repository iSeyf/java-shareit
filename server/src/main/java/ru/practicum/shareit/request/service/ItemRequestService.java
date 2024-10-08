package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(InputItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId, int from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);
}
