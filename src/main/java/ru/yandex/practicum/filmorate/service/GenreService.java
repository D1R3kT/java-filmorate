package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dal.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {
    private final JdbcGenreRepository jdbcGenreRepository;

    public GenreService(final JdbcGenreRepository jdbcGenreRepository) {
        this.jdbcGenreRepository = jdbcGenreRepository;
    }

    public Collection<Genre> getGenres() {
        return jdbcGenreRepository.findAll();
    }

    public Optional<Genre> getGenre(long id) {
        if (jdbcGenreRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        return jdbcGenreRepository.findById(id);
    }
}

