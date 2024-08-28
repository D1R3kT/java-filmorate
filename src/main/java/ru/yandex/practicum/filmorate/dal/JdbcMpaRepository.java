package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcMpaRepository extends BaseRepository<Mpa> implements MpaRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa ORDER BY mpa_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = :id;";

    public JdbcMpaRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }
}
