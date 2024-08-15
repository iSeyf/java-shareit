package ru.practicum.shareit.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.ErrorResponse;
import ru.practicum.shareit.exceptions.BookingConfirmationException;
import ru.practicum.shareit.exceptions.BookingModificationException;
import ru.practicum.shareit.exceptions.BookingOverlapException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnauthorizedCommentException;
import ru.practicum.shareit.exceptions.WrongUserException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class,
            BookingConfirmationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException exception) {
        return new ErrorResponse("404", exception.getMessage());
    }

    @ExceptionHandler({UnauthorizedCommentException.class,
            ItemUnavailableException.class,
            BookingModificationException.class,
            WrongUserException.class,
            BookingOverlapException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final RuntimeException exception) {
        return new ErrorResponse("400", exception.getMessage());
    }
}
