package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, genreMapper);
    }

    @Override
    public Genre getById(int id) {
        final String sql = "SELECT * FROM genre WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreMapper, id);
        if (genres.isEmpty()) {
            log.debug("Genre {} not found.", id);
            throw new NotFoundException("Genre not found");
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getByFilmId(int id) {
        String sqlQuery = "SELECT genre.id, genre.name " +
                "FROM genre " +
                "LEFT JOIN film_genre FG on genre.id = FG.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, genreMapper, id);
    }

    public void deleteFilmGenres(int filmId) {
        final String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, filmId);
    }

    public void addFilmGenres(int filmId, List<Genre> genres) {
        final String updateGenresQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre g : genres) {
            String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
            SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, filmId, g.getId());
            if (!checkRows.next()) {
                batchArgs.add(new Object[]{filmId, g.getId()});
            }
        }
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(updateGenresQuery, batchArgs);
        }
    }


}
