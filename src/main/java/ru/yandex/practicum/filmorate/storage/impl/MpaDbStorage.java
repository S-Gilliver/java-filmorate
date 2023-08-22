package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.Collection;
import java.util.List;

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
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!mpaRows.next()) {
            log.debug("Rating {} not found.", id);
            throw new NotFoundException("Rating not found");
        }
        return jdbcTemplate.queryForObject(sql, mpaMapper, id);
    }

    @Override
    public Mpa getByFilmId(int id) {
        String sqlQuery = "select m.id, m.name " +
                "from MPA AS m " +
                "join Films AS f ON m.ID = f.MPA_ID " +
                "where f.ID = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, mpaMapper, id);
        if (mpas.size() != 1) {
            return null;
        }
        return mpas.get(0);
    }
}
