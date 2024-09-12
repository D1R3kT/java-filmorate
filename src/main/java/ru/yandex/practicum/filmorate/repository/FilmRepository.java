package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Collection<Film> findAll();

    Optional<Film> findById(final long id);

    Film save(final Film film);

    Optional<Film> update(final Film film);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

}