package ru.yandex.practicum.filmorate.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.excepion.ConditionsMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmRepository implements InMemoryFilmRepository {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        log.info("Запрошен список всех фильмов");
        return films.values();
    }

    @Override
    public Film getFilm(Long id) {
        Film film = films.get(id);
        return film;
    }

    @Override
    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("name = null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Description.length > 200");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Release date is before 1895");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "дата релиза - не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Duration is negative");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("фильм добавлен: " + film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
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
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            log.info("фильм обновлен: " + oldFilm.toString());
            return oldFilm;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "фильм с id = " + newFilm.getId() + " не найден");
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
