package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.comparator.UserComparator;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ServiceValidator serviceValidator;

    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage, ServiceValidator serviceValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.serviceValidator = serviceValidator;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        if (!filmStorage.contains(id)) {
            throw new NotFoundException("The movie was not found!");
        }
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        serviceValidator.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.contains(film.getId())) {
            throw new NotFoundException("The movie with id " + film.getId() + " does not exist!");
        }
        serviceValidator.validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public String addLike(int filmId, int userId) {
        if (!filmStorage.contains(filmId) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            throw new BadRequestException("The user has already liked this movie!");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        film.setRate(film.getRate() + 1);
        log.info("Like it!");
        return ("To the user under the login "
                + userStorage.getUserById(userId).getLogin()
                + " liked the movie " + film.getName());
    }

    public String deleteLike(int filmId, int userId) {
        if (!filmStorage.contains(filmId) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            throw new BadRequestException("The user did not like this movie!");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        film.setRate(film.getRate() - 1);
        log.info("Removed the like!");
        return ("To the user under the login "
                + userStorage.getUserById(userId).getLogin()
                + " removed the like from the movie " + film.getName());
    }

    public List<Film> getPopularFilms(int count) {
        try {
            List<Film> list = new ArrayList<>(filmStorage.getFilms());
            list.sort(new UserComparator().reversed());
            if (count > filmStorage.getNames().size()) {
                count = filmStorage.getNames().size();
            }
            if (count < 1) {
                throw new NotFoundException("Count < 1");
            }
            return list.subList(0, count);
        } catch (Exception e) {
            throw new BadRequestException("There are no popular movies!");
        }
    }
}
