package ru.practicum.shareit.exceptions;

public class UnauthorizedCommentException extends RuntimeException {
    public UnauthorizedCommentException(String message) {
        super(message);
    }
}
