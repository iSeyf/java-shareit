package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(long userId, BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));

        if (!bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата start не может быть позже или равной end.");
        }
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> new NotFoundException("Предмет с таким ID не найден."));

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя бронировать свои вещи.");
        }
        if (!item.isAvailable()) {
            throw new ItemUnavailableException("Предмет недоступен.");
        }

        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        List<Booking> overlappingBookings = bookingRepository.findAllByItemIdAndEndAfterAndStartBefore(item.getId(), start, end);

        if (!overlappingBookings.isEmpty()) {
            throw new ValidationException("Обнаружены пересекающиеся бронирования.");
        }

        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingRequestDto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto respondBooking(long userId, long bookingId, boolean approved) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с указанным ID не найдено."));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование с указанным ID не найдено.");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingModificationException("Нельзя изменить бронирование.");
        }

        BookingStatus status;
        if (booking.getItem().getOwner().getId() != userId) {
            throw new BookingConfirmationException("Вы не можете подтвердить бронирование.");
        }

        if (approved) {
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }

        booking.setStatus(status);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с указанным ID не найдено."));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование с указанным ID не найдено.");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsByUserId(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        BookingState bookingState = BookingState.fromString(state);

        List<Booking> bookings = new ArrayList<>();
        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT ->
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING ->
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsByOwnerId(long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));

        BookingState bookingState = BookingState.fromString(state);

        List<Booking> bookings = new ArrayList<>();
        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                            LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case FUTURE ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case WAITING ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoList(bookings);
    }
}
