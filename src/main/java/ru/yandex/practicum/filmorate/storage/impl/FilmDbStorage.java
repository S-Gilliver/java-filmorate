package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, UserMapper userMapper,
                         GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.mapperUser = userMapper;
        this.genreDbStorage = genreDbStorage;
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
                "duration = ?, mpa_id = ? WHERE id = ?";
        if (film.getGenres() != null) {
            genreDbStorage.deleteFilmGenres(film.getId());
            genreDbStorage.addFilmGenres(film.getId(), film.getGenres());
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
        String sqlQuery = "SELECT f.* FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY  f.id " +
                "ORDER BY count(fl.user_id) DESC " +
                "LIMIT ?";

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

    public List<Film> getFilmsWithMpa() {
        String query = "SELECT f.id, f.description, f.name, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id";
        return jdbcTemplate.query(query, new FilmWithMpaRowMapper());
    }

    public Optional<Film> getFilmWithMpaById(Integer id) {
        String query = "SELECT f.id, f.description, f.name, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, new FilmWithMpaRowMapper()).stream().findFirst();
    }

    private class FilmWithMpaRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setDescription(rs.getString("description"));
            film.setName(rs.getString("name"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));

            film.setMpa(mpa);

            return film;
        }
    }
}
