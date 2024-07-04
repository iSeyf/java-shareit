package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

public class ValidationUtil {
    public static Booking checkBooking(long bookingId, long userId, BookingRepository bookingRepository) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Бронирование с указанным ID не найдено.");
        }
        Booking booking = optionalBooking.get();
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование с указанным ID не найдено.");
        }
        return booking;
    }

    public static User checkUser(long id, UserRepository userRepository) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
        return optionalUser.get();
    }

    public static Item checkItem(long id, ItemRepository itemRepository) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с таким ID не найден.");
        }
        return optionalItem.get();
    }
}
