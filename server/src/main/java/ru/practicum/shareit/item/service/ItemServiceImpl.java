package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        Item item = ItemMapper.toItem(itemDto, user);
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с таким ID не найден."));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long id, ItemDto itemDto, long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с таким ID не найден."));

        if (item.getOwner().getId() != ownerId) {
            throw new NotFoundException("Предмет не найден.");
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long id, long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с таким ID не найден."));
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        itemDto.setComments(CommentMapper.toCommentDtoList(comments));

        if (item.getOwner().getId() == ownerId) {
            setLastAndNextBooking(itemDto);
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(long ownerId) {
        List<Item> itemList = itemRepository.findAllItemByOwnerId(ownerId);
        List<ItemDto> itemDtoList = ItemMapper.toItemDtoList(itemList);
        for (ItemDto itemDto : itemDtoList) {
            List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
            itemDto.setComments(CommentMapper.toCommentDtoList(comments));
            setLastAndNextBooking(itemDto);
        }
        return itemDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            List<Item> itemList = itemRepository.searchItem(text);
            itemDtoList = ItemMapper.toItemDtoList(itemList);
            for (ItemDto itemDto : itemDtoList) {
                List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
                itemDto.setComments(CommentMapper.toCommentDtoList(comments));
                setLastAndNextBooking(itemDto);
            }
        }
        return itemDtoList;
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, InputCommentDto commentDto, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с таким ID не найден."));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));

        List<Booking> bookingList = bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookingList.isEmpty()) {
            throw new UnauthorizedCommentException("Вы не можете комментировать вещи, которые не брали.");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto setLastAndNextBooking(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(itemDto.getId(), BookingStatus.APPROVED);

        TreeSet<Booking> bookingsByStartTime = new TreeSet<>(Comparator.comparing(Booking::getStart));
        bookingsByStartTime.addAll(bookings);

        LocalDateTime now = LocalDateTime.now();
        Booking referenceBooking = new Booking();
        referenceBooking.setStart(now);

        Booking nextBooking = bookingsByStartTime.ceiling(referenceBooking);
        Booking lastBooking = bookingsByStartTime.floor(referenceBooking);

        itemDto.setNextBooking(nextBooking != null ? new BookingInfoDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null);
        itemDto.setLastBooking(lastBooking != null ? new BookingInfoDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null);

        return itemDto;
    }
}
