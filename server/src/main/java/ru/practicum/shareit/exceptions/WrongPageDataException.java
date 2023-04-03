package ru.practicum.shareit.exceptions;

public class WrongPageDataException extends RuntimeException {
    public WrongPageDataException(String message) {
        super(message);
    }
}
