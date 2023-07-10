package ru.practicum.shareit.excеption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}