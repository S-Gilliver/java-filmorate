package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

    Collection<Genre> getAllGenres();

    Collection<Genre> getGenresByFilmId(int filmId);

    Genre getGenreById(int genreId);

    boolean addFilmGenres(int filmId, Collection<Genre> genres);

    boolean deleteFilmGenres(int filmId);
}
