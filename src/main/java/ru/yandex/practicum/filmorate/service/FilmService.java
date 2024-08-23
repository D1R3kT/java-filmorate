package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.excepion.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

@Component
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public FilmService(UserRepository userRepository, FilmRepository filmRepository) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmRepository.getFilm(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Фильм с id = " + filmId + " не найден");
        }
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + " не найден");
        }
        Set<Long> usersLike = likes.computeIfAbsent(filmId, id -> new HashSet<>());
        usersLike.add(userId);
        likes.put(filmId, usersLike);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmRepository.getFilm(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Фильм с id = " + filmId + " не найден");
        }
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + " не найден");
        }
        Set<Long> usersLikes = likes.get(filmId);
        usersLikes.remove(userId);
        likes.put(filmId, usersLikes);
    }

    public Collection<Film> getTopFilms(int count) {
        if (count < 1) {
            throw new ValidationException("некорректное число");
        }
        return likes.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().size(), reverseOrder()))
                .map(entry -> filmRepository.getFilm(entry.getKey()))
                .limit(count)
                .collect(Collectors.toList());
    }
}





