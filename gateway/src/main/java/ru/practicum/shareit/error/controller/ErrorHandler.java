package ru.practicum.shareit.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.error.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final RuntimeException exception) {
        return new ErrorResponse("400", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerUnsupportedStateException(final UnsupportedStateException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}