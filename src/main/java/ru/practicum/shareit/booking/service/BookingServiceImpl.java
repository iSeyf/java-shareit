package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingConfirmationException;
import ru.practicum.shareit.exceptions.BookingModificationException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto addBooking(long userId, BookingRequestDto bookingRequestDto) {
        User user = ValidationUtil.checkUser(userId, userRepository);

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата start не может быть позже end.");
        }
        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата end не может быть той же что и start.");
        }
        Item item = ValidationUtil.checkItem(bookingRequestDto.getItemId(), itemRepository);

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя бронировать свои вещи.");
        }
        if (!item.isAvailable()) {
            throw new ItemUnavailableException("Предмет недоступен.");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingRequestDto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto respondBooking(long userId, long bookingId, boolean approved) {
        ValidationUtil.checkUser(userId, userRepository);
        Booking booking = ValidationUtil.checkBooking(bookingId, userId, bookingRepository);

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingModificationException("Нельзя изменить бронирование.");
        }

        BookingStatus status;
        if (booking.getItem().getOwner().getId() == userId) {
            if (approved) {
                status = BookingStatus.APPROVED;
            } else {
                status = BookingStatus.REJECTED;
            }
        } else {
            if (!approved) {
                status = BookingStatus.CANCELED;
            } else {
                throw new BookingConfirmationException("Вы не можете подтвердить бронирование.");
            }
        }

        booking.setStatus(status);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = ValidationUtil.checkBooking(bookingId, userId, bookingRepository);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(long userId, String state) {
        ValidationUtil.checkUser(userId, userRepository);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                Collections.reverse(bookings);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        Collections.reverse(bookings);
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(long ownerId, String state) {
        ValidationUtil.checkUser(ownerId, userRepository);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        Collections.reverse(bookings);
        return BookingMapper.toBookingDtoList(bookings);
    }
}
