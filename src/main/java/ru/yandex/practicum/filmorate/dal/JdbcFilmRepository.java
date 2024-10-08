package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {

    private static final String FIND_ALL_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            ORDER BY film_id;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            ORDER BY COALESCE (l.likes, 0) DESC
            LIMIT :limit
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            WHERE film_id = :id;
            """;
    private static final String SAVE_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM (
              SELECT *
              FROM FINAL TABLE (
                INSERT INTO films (film_name, description, release_date, duration, mpa_id)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId)
              )
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
            """;
    private static final String UPDATE_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM (
              SELECT *
              FROM FINAL TABLE (
                UPDATE films
                SET film_name = :name,
                  description = :description,
                  release_date = :releaseDate,
                  duration = :duration,
                  mpa_id = :mpaId
                WHERE film_id = :id
              )
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
            """;
    private static final String ADD_LIKE_QUERY = """
            MERGE INTO likes
            KEY (film_id, user_id)
            VALUES (:id, :userId);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM likes
            WHERE film_id = :id AND user_id = :userId;
            """;
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films;";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = """
            SELECT g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = :id
            ORDER BY g.genre_id;
            """;
    private static final String FIND_GENRES_BY_FILM_IDS_QUERY = """
            SELECT fg.film_id,
              g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY g.genre_id;
            """;
    private static final String SAVE_FILM_GENRE_QUERY = """
            MERGE INTO film_genres
            KEY (film_id, genre_id)
            VALUES (:id, :genreId);
            """;
    private static final String DELETE_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id AND genre_id NOT IN (%s);
            """;
    private static final String DELETE_ALL_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id;
            """;

    private final RowMapper<Genre> genreMapper;

    @Autowired
    public JdbcFilmRepository(final NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<Genre> genreMapper) {
        super(jdbc, mapper);
        this.genreMapper = genreMapper;
    }

    @Override
    public Collection<Film> findAll() {
        return supplementWithGenres(findAll(FIND_ALL_QUERY));
    }

    public Collection<Film> findAllOrderByLikesDesc(long limit) {
        var params = new MapSqlParameterSource("limit", limit);
        return supplementWithGenres(findMany(FIND_ALL_ORDER_BY_LIKES_DESC, params));
    }

    @Override
    public Optional<Film> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id).map(this::supplementWithGenres);
    }

    @Override
    public Film save(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        Film savedFilm = findOne(SAVE_QUERY, params).orElseThrow();
        saveFilmGenres(savedFilm.getId(), film.getGenres());
        return supplementWithGenres(savedFilm);
    }

    @Override
    public Optional<Film> update(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                // Use string representation due to bug with dates before 1900-01-02
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        Optional<Film> savedFilm = findOne(UPDATE_QUERY, params);
        savedFilm.ifPresent(f -> {
            saveFilmGenres(film.getId(), film.getGenres());
            deleteFilmGenresExcept(film.getId(), film.getGenres());
        });
        return savedFilm.map(this::supplementWithGenres);
    }

    @Override
    public void addLike(long id, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        execute(ADD_LIKE_QUERY, params);
    }

    @Override
    public void deleteLike(long id, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        execute(DELETE_LIKE_QUERY, params);
    }

    public void delete(final long id) {
        delete(DELETE_QUERY, id);
    }

    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }

    private Film supplementWithGenres(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, params, genreMapper);
        film.setGenres(genres);
        return film;
    }

    private Collection<Film> supplementWithGenres(final Collection<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Genre>> genresByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_GENRES_BY_FILM_IDS_QUERY.formatted(filmIds),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Genre genre = genreMapper.mapRow(rs, 0);
                    genresByFilmId.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
                }
        );
        return films.stream()
                .peek(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new HashSet<>())))
                .toList();
    }

    private void saveFilmGenres(final long id, final Collection<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            SqlParameterSource[] params = genres.stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("genreId", genre.getId())
                    )
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SAVE_FILM_GENRE_QUERY, params);
        }
    }

    private void deleteFilmGenresExcept(final long id, final Collection<Genre> genres) {
        var params = new MapSqlParameterSource("id", id);
        if (genres == null || genres.isEmpty()) {
            execute(DELETE_ALL_FILM_GENRES_QUERY, params);
        } else {
            final String genreIds = genres.stream()
                    .map(Genre::getId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            execute(DELETE_FILM_GENRES_QUERY.formatted(genreIds), params);
        }
    }
}



