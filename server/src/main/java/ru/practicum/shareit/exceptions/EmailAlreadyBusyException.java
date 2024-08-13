package ru.practicum.shareit.exceptions;

public class EmailAlreadyBusyException extends RuntimeException {
    public EmailAlreadyBusyException(String message) {
        super(message);
    }
}
