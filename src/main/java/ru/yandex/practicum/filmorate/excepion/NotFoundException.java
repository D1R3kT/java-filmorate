package ru.yandex.practicum.filmorate.excepion;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
