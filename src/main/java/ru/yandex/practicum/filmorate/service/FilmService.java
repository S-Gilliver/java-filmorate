package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;

    private final FilmDbStorage filmDbStorage;

    public List<Film> getFilms() {
        List<Film> films = filmDbStorage.getFilmsWithMpa();
        films.forEach(f -> {
            f.setGenres(genreStorage.getByFilmId(f.getId()));
        });
        return films;
    }

    public Film getFilmById(Integer id) {
        Film film = filmDbStorage.getFilmWithMpaById(id)
                .orElseThrow(() -> new NotFoundException("The movie was not found!"));
        log.info("The film was received!");
        film.setGenres(genreStorage.getByFilmId(id));
        return film;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        containsFilm(film.getId());
        Film resultFilm = filmStorage.updateFilm(film);
        resultFilm.setGenres(genreStorage.getByFilmId(film.getId()));
        return resultFilm;
    }

    public void deleteFilm(int id) {
        containsFilm(id);
        filmStorage.deleteFilm(id);
    }

    public void addLike(Integer id, Integer userId) {
        containsFilm(id);
        containsUser(userId);
        filmStorage.addLike(id, userId);
        log.info(String.valueOf(filmStorage.getUserById(userId)));
    }

    public void deleteLike(Integer id, Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("id < 1");
        }
        containsFilm(id);
        containsUser(userId);
        filmStorage.deleteLike(id, userId);
        log.info(String.valueOf(filmStorage.getUserById(userId)));
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(f -> {
            f.setGenres(genreStorage.getByFilmId(f.getId()));
            f.setMpa(mpaStorage.getByFilmId(f.getId()));
        });
        return films;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Cinema hasn't been invented yet!");
        }
        if (film.getDuration() == null) {
            throw new ValidationException("An error in the duration of the movie!");
        }
    }

    private void containsFilm(int id) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("The movie with id does not exist!"));
    }

    private void containsUser(int id) {
        filmStorage.getUserById(id).orElseThrow(() -> new NotFoundException("The user with id does not exist!"));
    }
}
