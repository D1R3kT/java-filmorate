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

import static java.util.stream.Collectors.toMap;

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


    //  поставить лайк
    public void addLike(Long filmId, Long userId) {
        if (filmRepository.getFilm(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Фильм с id = " + filmId + " не найден");
        }
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + " не найден");
        }
        Set<Long> usersLike = likes.computeIfAbsent(userId, id -> new HashSet<>());
        usersLike.add(userId);
        likes.put(filmId, usersLike);

    }

    //  удалить лайк
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

    //  вывести список фильмов в количестве count
    public Set<Film> getTopFilms(int count) {
        if (count < 0) {
            throw new ValidationException("Количество фильмов не может быть отрицательным");
        }
        Set<Film> topFilms = new HashSet<>();
        Map<Long, Set<Long>> sorted = likes.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().size()))
                .limit(count)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));
        for (Long id : sorted.keySet()) {
            topFilms.add(filmRepository.getFilm(id));
        }
        return topFilms;
    }


}
