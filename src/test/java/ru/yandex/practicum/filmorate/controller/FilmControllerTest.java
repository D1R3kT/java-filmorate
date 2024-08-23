package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmRepository;

import java.time.LocalDate;

public class FilmControllerTest {

    @Test
    @DisplayName("Name")
    void shouldCreateFilmWithoutName() {
        FilmRepository filmService = new InMemoryFilmRepository();
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2023, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                    " \"название не может быть пустым\"", exp.getMessage());
        }
    }


    @Test
    @DisplayName("Description length")
    void shouldCreateFilmWithDescription() {
        FilmRepository filmService = new InMemoryFilmRepository();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("qweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqeqweqweqeqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqeqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqeqewqweqweqweqweqweqweqeqweqweqweqweq");
        film.setReleaseDate(LocalDate.of(2023, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                    " \"максимальная длина описания - 200 символов\"", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Release Date")
    void shouldCreateFilmWithDateBefore1895() {
        FilmRepository filmService = new InMemoryFilmRepository();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1800, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                    " \"дата релиза - не раньше 28 декабря 1895 года\"", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Duration")
    void shouldCreateFilmWithNegativeDuration() {
        FilmRepository filmService = new InMemoryFilmRepository();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1900, 10, 10));
        film.setDuration(Long.valueOf(-200));

        try {
            filmService.create(film);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                    " \"продолжительность фильма должна быть положительным числом\"", exp.getMessage());
        }
    }


    @Test
    @DisplayName("Update film")
    void shouldUpdateFilmWithoutId() {
        FilmRepository filmService = new InMemoryFilmRepository();
        Film film = new Film();
        try {
            filmService.update(film);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND + " \"фильм с id = " + film.getId() + " не найден\"",
                    exp.getMessage());
        }
    }
}
