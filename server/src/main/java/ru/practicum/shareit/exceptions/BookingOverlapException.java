package ru.practicum.shareit.exceptions;

public class BookingOverlapException extends RuntimeException {
    public BookingOverlapException(String message) {
        super(message);
    }
}
