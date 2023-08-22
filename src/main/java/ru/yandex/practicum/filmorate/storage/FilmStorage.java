package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    boolean contains(Integer id);

    Film updateFilm(Film film);

    boolean deleteFilm(Film film);

    boolean addLike(Integer id, Integer userId);

    boolean deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);
}
