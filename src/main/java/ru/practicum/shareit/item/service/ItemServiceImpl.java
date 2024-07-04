package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.UnauthorizedCommentException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        User user = ValidationUtil.checkUser(ownerId, userRepository);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long id, ItemDto itemDto, long ownerId) {
        Item item = ValidationUtil.checkItem(id, itemRepository);

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
    public ItemDto getItemById(long id, long ownerId) {
        Item item = ValidationUtil.checkItem(id, itemRepository);
        ValidationUtil.checkUser(ownerId, userRepository);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        itemDto.setComments(CommentMapper.toCommentDtoList(comments));

        if (item.getOwner().getId() == ownerId) {
            setLastAndNextBooking(itemDto);
        }

        return itemDto;
    }

    @Override
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
    public CommentDto addComment(long itemId, InputCommentDto commentDto, long userId) {
        Item item = ValidationUtil.checkItem(itemId, itemRepository);
        User user = ValidationUtil.checkUser(userId, userRepository);

        List<Booking> bookingList = bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookingList.isEmpty()) {
            throw new UnauthorizedCommentException("Вы не можете комментировать вещи, которые не брали.");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto setLastAndNextBooking(ItemDto itemDto) {
        List<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemDto.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        List<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemDto.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (!lastBooking.isEmpty()) {
            Booking booking = lastBooking.get(0);
            itemDto.setLastBooking(new BookingInfoDto(booking.getId(), booking.getBooker().getId()));
        }
        if (!nextBooking.isEmpty()) {
            Booking booking = nextBooking.get(0);
            itemDto.setNextBooking(new BookingInfoDto(booking.getId(), booking.getBooker().getId()));
        }
        return itemDto;
    }
}
