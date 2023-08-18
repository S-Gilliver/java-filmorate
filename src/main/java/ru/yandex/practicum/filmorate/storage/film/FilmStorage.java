package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    boolean contains(Integer id);

    Set<String> getNames();

    Film updateFilm(Film film);
}
