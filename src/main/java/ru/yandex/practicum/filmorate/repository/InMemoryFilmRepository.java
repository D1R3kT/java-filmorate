package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface InMemoryFilmRepository {
    Collection<Film> getAll();

    Film getFilm(Long id);

    Film create(Film film);

    Film update(Film newFilm);
}
