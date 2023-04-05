package ru.practicum.shareit.exceptions;

public class WrongDataUpdateException extends RuntimeException {
    public WrongDataUpdateException(String message) {
        super(message);
    }
}
