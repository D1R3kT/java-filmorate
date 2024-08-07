package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.excepion.ConditionsMetException;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

public class FilmControllerTest {

    @Test
    @DisplayName("Name")
    void shouldCreateFilmWithoutName() {
        FilmService filmService = new FilmService();
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2023, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ConditionsMetException exp) {
            Assertions.assertEquals("название не может быть пустым", exp.getMessage());
        }
    }


    @Test
    @DisplayName("Description length")
    void shouldCreateFilmWithDescription() {
        FilmService filmService = new FilmService();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("qweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqeqweqweqeqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqeqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqeqewqweqweqweqweqweqweqeqweqweqweqweq");
        film.setReleaseDate(LocalDate.of(2023, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ConditionsMetException exp) {
            Assertions.assertEquals("максимальная длина описания - 200 символов", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Release Date")
    void shouldCreateFilmWithDateBefore1895() {
        FilmService filmService = new FilmService();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1800, 10, 10));
        film.setDuration(Long.valueOf(200));

        try {
            filmService.create(film);
        } catch (ConditionsMetException exp) {
            Assertions.assertEquals("дата релиза - не раньше 28 декабря 1895 года", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Duration")
    void shouldCreateFilmWithNegativeDuration() {
        FilmService filmService = new FilmService();
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1900, 10, 10));
        film.setDuration(Long.valueOf(-200));

        try {
            filmService.create(film);
        } catch (ConditionsMetException exp) {
            Assertions.assertEquals("продолжительность фильма должна быть положительным числом", exp.getMessage());
        }
    }


    @Test
    @DisplayName("Update film")
    void shouldUpdateFilmWithoutId() {
        FilmService filmService = new FilmService();
        Film film = new Film();
        try {
            filmService.update(film);
        } catch (NotFoundException exp) {
            Assertions.assertEquals("фильм с id = " + film.getId() + " не найден", exp.getMessage());
        }
    }
}
