package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, BookingRequestDto bookingRequestDto);

    BookingDto respondBooking(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getAllBookingsByUserId(long userId, BookingState state);

    List<BookingDto> getAllBookingsByOwnerId(long ownerId, BookingState state);
}
