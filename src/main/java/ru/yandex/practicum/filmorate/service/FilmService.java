package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dal.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;


@Component
public class FilmService {

    private final JdbcFilmRepository jdbcFilmRepository;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;


    public FilmService(final JdbcFilmRepository jdbcFilmRepository,
                       final UserService userService, MpaService mpaService, GenreService genreService) {
        this.jdbcFilmRepository = jdbcFilmRepository;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Collection<Film> getFilms() {
        return jdbcFilmRepository.findAll();
    }

    public Collection<Film> getTopFilmsByLikes(final long limit) {
        return jdbcFilmRepository.findAllOrderByLikesDesc(limit);
    }

    public Optional<Film> getFilm(final long id) {
        return jdbcFilmRepository.findById(id);
    }

    public Film createFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film name is empty");
        }
        if (film.getDescription() == null || film.getDescription().isEmpty() || film.getDescription().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film description is empty");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "дата релиза - не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "продолжительность фильма должна быть положительным числом");
        }
        if (!mpaService.getMpas().contains(film.getMpa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mpa не найден");
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (!genreService.getGenres().contains(genre)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
        }

        final Film filmStored = jdbcFilmRepository.save(film);
        return filmStored;
    }

    public Optional<Film> updateFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        final Optional<Film> filmStored = jdbcFilmRepository.update(film);
        return filmStored;
    }

    public void addLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        jdbcFilmRepository.addLike(id, userId);
    }

    public void deleteLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        jdbcFilmRepository.deleteLike(id, userId);
    }

    private void assertFilmExist(final long id) {
        jdbcFilmRepository.findById(id).orElseThrow(() -> new NotFoundException(Film.class, id));
    }

    private void assertUserExist(final long userId) {
        userService.getUser(userId).orElseThrow(() -> new NotFoundException(User.class, userId));
    }
}





