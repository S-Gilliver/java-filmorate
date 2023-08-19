package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    boolean contains(Integer id);

    Film updateFilm(Film film);
}
