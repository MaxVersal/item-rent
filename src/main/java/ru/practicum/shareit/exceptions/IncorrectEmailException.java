package ru.practicum.shareit.exceptions;

public class IncorrectEmailException extends RuntimeException {

    public IncorrectEmailException(String message) {
        super(message);
    }
}
