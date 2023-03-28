package ru.practicum.shareit.exceptions;

public class IncorrectBookingException extends RuntimeException {
    public IncorrectBookingException(String message) {
        super(message);
    }
}
