package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

@Slf4j
@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public Mpa findAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.queryForObject(sql, mpaMapper);
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mpaMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Rating {} not found.", id);
            throw new NotFoundException("Rating not found");
        }
    }


    @Override
    public Mpa getByFilmId(int id) {
        String sqlQuery = "select m.id, m.name " +
                "from MPA AS m " +
                "join Films AS f ON m.ID = f.MPA_ID " +
                "where f.ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, mpaMapper,id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
