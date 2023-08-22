package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getFilms();

    Optional<Film> getFilmById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count);

    Optional<User> getUserById(Integer userId);
}
