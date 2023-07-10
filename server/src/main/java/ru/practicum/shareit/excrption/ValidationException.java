package ru.practicum.shareit.excrption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}