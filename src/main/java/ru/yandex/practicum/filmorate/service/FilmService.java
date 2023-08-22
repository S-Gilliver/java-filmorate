package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        if (!filmStorage.contains(id)) {
            throw new NotFoundException("The movie was not found!");
        }
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.contains(film.getId())) {
            throw new NotFoundException("The movie with id " + film.getId() + " does not exist!");
        }
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer id, Integer userId) {
        if (!filmStorage.contains(id) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (filmStorage.getFilmById(id).getLikeIds().contains(userId)) {
            throw new BadRequestException("The user has already liked this movie!");
        }
        filmStorage.addLike(id, userId);
        log.info(String.valueOf(userStorage.getUserById(userId)));
    }

    public void deleteLike(Integer id, Integer userId) {
        if (!filmStorage.contains(id) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (!filmStorage.getFilmById(id).getLikeIds().contains(userId)) {
            throw new BadRequestException("The user did not like this movie!");
        }
        filmStorage.deleteLike(id, userId);
        log.info(String.valueOf(userStorage.getUserById(userId)));
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == Integer.MIN_VALUE) {
            count = 10;
        }
        return filmStorage.getPopularFilms(count);
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
