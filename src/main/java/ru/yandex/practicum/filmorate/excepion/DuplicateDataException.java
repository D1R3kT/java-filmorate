package ru.yandex.practicum.filmorate.excepion;

public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message) {
        super(message);
    }
}
