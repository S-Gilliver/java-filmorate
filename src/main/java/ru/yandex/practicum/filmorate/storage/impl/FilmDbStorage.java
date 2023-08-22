package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final UserMapper mapperUser;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.mapperUser = userMapper;
    }

    @Override
    public List<Film> getFilms() {
        final String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, filmMapper);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        try {
            final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, filmMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film addFilm(Film film) {
        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());

            return stmt;
        }, generatedId);

        film.setId(Objects.requireNonNull(generatedId.getKey()).intValue());

        if (film.getGenres() != null) {

            final String genresSqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

            this.jdbcTemplate.batchUpdate(
                    genresSqlQuery,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Genre genre = film.getGenres().get(i);
                            ps.setString(1, String.valueOf(film.getId()));
                            ps.setString(2, String.valueOf(genre.getId()));
                        }

                        @Override
                        public int getBatchSize() {
                            return film.getGenres().size();
                        }
                    });
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE id = ?";

        if (film.getGenres() != null) {
            final String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
            final String updateGenresQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.update(deleteGenresQuery, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Integer filmId) {
        final String genresSqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        String mpaSqlQuery = "DELETE FROM mpa WHERE id = ?";

        jdbcTemplate.update(genresSqlQuery, filmId);
        jdbcTemplate.update(mpaSqlQuery, filmId);
        final String sqlQuery = "DELETE FROM films WHERE id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        final String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        log.info("User liked the movie");
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        final String sqlQuery = "DELETE FROM film_likes " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("User delete like");
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "select f.* from films f " +
                "left join film_likes fl on f.id = fl.film_id " +
                "group by  f.id " +
                "order by count(fl.user_id) desc " +
                "limit ?";

        return jdbcTemplate.query(sqlQuery, filmMapper, count);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        try {
            final String checkUserQuery = "SELECT * FROM users WHERE id = ?";
            return Optional.of(jdbcTemplate
                    .queryForObject(checkUserQuery, mapperUser, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
