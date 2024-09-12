package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dal.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Service
public class MpaService {
    private final JdbcMpaRepository jdbcMpaRepository;

    public MpaService(final JdbcMpaRepository jdbcMpaRepository) {
        this.jdbcMpaRepository = jdbcMpaRepository;
    }

    public Collection<Mpa> getMpas() {

        return jdbcMpaRepository.findAll();
    }

    public Optional<Mpa> getMpa(final long id) {
        if (jdbcMpaRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return jdbcMpaRepository.findById(id);
    }
}
