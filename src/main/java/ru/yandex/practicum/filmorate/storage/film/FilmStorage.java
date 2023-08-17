package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilmById(int id);

    Film addFilm(Film film);

    boolean contains(int id);

    Film updateFilm(Film film);
}
