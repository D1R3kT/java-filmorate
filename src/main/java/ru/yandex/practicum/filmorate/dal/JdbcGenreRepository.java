package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :id;";

    public JdbcGenreRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }
}
