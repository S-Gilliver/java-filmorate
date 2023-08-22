package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
    }

    @Override
    public Film getFilmById(Integer id) {

        String sqlFilm = "select * from FILM " +
                "INNER JOIN RATING_MPA R on FILM.MPA_ID = R.RATING_ID " +
                "where ID = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlFilm, (rs, rowNum) -> makeFilm(rs), id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с идентификатором " +
                    id + " не зарегистрирован!");
        }
        log.info("Найден фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "select * from FILM " +
                "INNER JOIN RATING_MPA R on FILM.MPA_ID = R.RATING_ID ";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeFilm(resultSet));
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into FILM " +
                "(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());
            preparedStatement.setInt(6, Math.toIntExact(film.getMpa().getId()));
            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikeIds() != null) {
            for (Integer userId : film.getLikeIds()) {
                addLike(film.getId(), userId);
            }
        }
        return getFilmById(id);
    }

    @Override
    public boolean contains(Integer id) {
        return false;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILM " +
                "set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ? ,MPA_ID = ? " +
                "where ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        genreService.deleteFilmGenres(film.getId());
        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }

        if(film.getLikeIds() != null) {
            for (Integer userId : film.getLikeIds()) {
                addLike(film.getId(), userId);
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlQuery = "delete from FILM where ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        return true;
    }

    @Override
    public boolean addLike(Integer id, Integer userId) {
        String sql = "select * from LIKES where USER_ID = ? and FILM_ID = ?";
        SqlRowSet existLike = jdbcTemplate.queryForRowSet(sql, userId, id);
        if (!existLike.next()) {
            String setLike = "insert into LIKES (USER_ID, FILM_ID) values (?, ?) ";
            jdbcTemplate.update(setLike, userId, id);
        }
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId, id);
        log.info(String.valueOf(sqlRowSet.next()));
        return sqlRowSet.next();
    }

    @Override
    public boolean deleteLike(Integer id, Integer userId) {
        String deleteLike = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(deleteLike, id, userId);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlMostPopular = "select count(L.LIKE_ID) as likeRate" +
                ",FILM.ID" +
                ",FILM.NAME ,FILM.DESCRIPTION ,RELEASE_DATE ,DURATION ,RATE ,R.RATING_ID, R.NAME, R.DESCRIPTION from FILM " +
                "left join LIKES L on L.FILM_ID = FILM.ID " +
                "inner join RATING_MPA R on R.RATING_ID = FILM.MPA_ID " +
                "group by FILM.ID " +
                "ORDER BY likeRate desc " +
                "limit ?";
        return jdbcTemplate.query(sqlMostPopular, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int filmId = resultSet.getInt("ID");
        return new Film(
                filmId,
                resultSet.getString("Name"),
                resultSet.getString("Description"),
                Objects.requireNonNull(resultSet.getDate("Release_Date")).toLocalDate(),
                resultSet.getInt("Duration"),
                resultSet.getInt("Rate"),
                new Mpa(resultSet.getInt("Rating_MPA.Rating_ID"),
                        resultSet.getString("Rating_MPA.Name"),
                        resultSet.getString("Rating_MPA.Description")),
                (List<Genre>) genreService.getFilmGenres(filmId),
                (Set<Integer>) getFilmLikes(filmId));
    }

    private List<Integer> getFilmLikes(int filmId) {
        String sqlGetLikes = "select USER_ID from LIKES where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlGetLikes, Integer.class, filmId);
    }

}
