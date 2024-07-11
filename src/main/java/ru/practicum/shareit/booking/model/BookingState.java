package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.UnsupportedStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String state) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return bookingState;
            }
        }
        throw new UnsupportedStateException("Unknown state: " + state);
    }
}
