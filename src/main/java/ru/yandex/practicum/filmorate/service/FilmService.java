package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
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

    public String addLike(Integer id, Integer userId) {
        if (!filmStorage.contains(id) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (filmStorage.getFilmById(id).getLikes().contains(userId)) {
            throw new BadRequestException("The user has already liked this movie!");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
        film.setRate(film.getRate() + 1);
        log.info("Like it!");
        return ("To the user under the login "
                + userStorage.getUserById(userId).getLogin()
                + " liked the movie " + film.getName());
    }

    public String deleteLike(Integer id, Integer userId) {
        if (!filmStorage.contains(id) || !userStorage.contains(userId)) {
            throw new NotFoundException("There is no such movie or user!");
        }
        if (!filmStorage.getFilmById(id).getLikes().contains(userId)) {
            throw new BadRequestException("The user did not like this movie!");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
        film.setRate(film.getRate() - 1);
        log.info("Removed the like!");
        return ("To the user under the login "
                + userStorage.getUserById(userId).getLogin()
                + " removed the like from the movie " + film.getName());
    }

    public List<Film> getPopularFilms(Integer count) {
        return getFilms()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted((t1, t2) -> t2.getLikes().size() - t1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
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
