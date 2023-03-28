package ru.practicum.shareit.exceptions;

public class ItemAlreadyInUseException extends RuntimeException {

    public ItemAlreadyInUseException(String message) {
        super(message);
    }
}
