package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excepion.ConditionsMetException;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрошен список всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("name = null");
            throw new ConditionsMetException("название не может быть пустым");

        }
        if (film.getDescription().length() > 200) {
            log.error("Description.length > 200");
            throw new ConditionsMetException("максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Release date is before 1895");
            throw new ConditionsMetException("дата релиза - не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Duration is negative");
            throw new ConditionsMetException("продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("фильм добавлен: " + film.toString());
        return film;
    }

    @PutMapping
    public Film update (@RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getId() == null) {
                log.error("id = null");
                throw new ConditionsMetException("id должен быть указан");
            }
            if (newFilm.getName() != null || newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && newFilm.getDescription().length() < 200) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(LocalDate.of(1895,12,28))) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            log.info("фильм обновлен: " + oldFilm.toString());
            return oldFilm;
        }
        throw new NotFoundException("фильм с id = " + newFilm.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
