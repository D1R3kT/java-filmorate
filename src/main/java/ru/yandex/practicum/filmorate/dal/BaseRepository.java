package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.*;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected List<T> findAll(final String query) {
        return jdbc.query(query, mapper);
    }

    protected Optional<T> findById(final String query, final Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, Map.of("id", id), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected void delete(final String query, final Long id) {
        jdbc.update(query, Map.of("id", id));
    }

    protected void execute(final String query) {
        jdbc.update(query, Collections.emptyMap());
    }

    protected void execute(final String query, final SqlParameterSource param) {
        jdbc.update(query, param);
    }

    protected Optional<T> findOne(final String query, final SqlParameterSource params) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, params, mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(final String query, final SqlParameterSource params) {
        return jdbc.query(query, params, mapper);
    }
}
