package ru.yandex.practicum.filmorate.excepion;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
