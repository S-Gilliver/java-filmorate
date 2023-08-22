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
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!genreRows.next()) {
            log.debug("Genre {} not found.", id);
            throw new NotFoundException("Genre not found");
        }
        return jdbcTemplate.queryForObject(sql, genreMapper, id);
    }

    @Override
    public List<Genre> getByFilmId(int id) {
        String sqlQuery = "SELECT genre.id, genre.name " +
                "FROM genre " +
                "LEFT JOIN film_genre FG on genre.id = FG.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, genreMapper, id);
    }
}
