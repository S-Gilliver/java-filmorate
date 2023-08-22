package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final GenreStorage genreDbStorage;

    private final MpaStorage mpaDbStorage;

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        films.forEach(f -> {
            f.setGenres(genreDbStorage.getByFilmId(f.getId()));
            f.setMpa(mpaDbStorage.getByFilmId(f.getId()));
        });
        return films;
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("The movie was not found!"));
        log.info("The film was received!");
        film.setGenres(genreDbStorage.getByFilmId(id));
        film.setMpa(mpaDbStorage.getByFilmId(id));
        return film;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmStorage.getFilmById(film.getId()).orElseThrow(() -> new NotFoundException("The movie with id " + film.getId() + " does not exist!"));
        validateFilm(film);
        Film resultFilm = filmStorage.updateFilm(film);
        resultFilm.setGenres(genreDbStorage.getByFilmId(film.getId()));
        return resultFilm;
    }

    public void deleteFilm(int id) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("The movie with id " +
                filmStorage.getFilmById(id).get().getId() + " does not exist!"));
        filmStorage.deleteFilm(id);
    }

    public void addLike(Integer id, Integer userId) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("The movie with id " +
                filmStorage.getFilmById(id).get().getId() + " does not exist!"));
        filmStorage.getUserById(id).orElseThrow(() -> new NotFoundException("The user with id " +
                filmStorage.getUserById(id).get().getId() + " does not exist!"));
        filmStorage.addLike(id, userId);
        log.info(String.valueOf(filmStorage.getUserById(userId)));
    }

    public void deleteLike(Integer id, Integer userId) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("The movie with id " +
                filmStorage.getFilmById(id).get().getId() + " does not exist!"));
        filmStorage.getUserById(id).orElseThrow(() -> new NotFoundException("The user with id " +
                filmStorage.getUserById(id).get().getId() + " does not exist!"));
        filmStorage.deleteLike(id, userId);
        log.info(String.valueOf(filmStorage.getUserById(userId)));
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(f -> {
            f.setGenres(genreDbStorage.getByFilmId(f.getId()));
            f.setMpa(mpaDbStorage.getByFilmId(f.getId()));
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
}
