package ru.practicum.shareit.exceptions;

public class SelfBookingException extends RuntimeException {
    public SelfBookingException(String message) {
        super(message);
    }
}
