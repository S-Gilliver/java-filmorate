package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {

    Collection<Genre> findAll();

    Genre getById(int id);

    List<Genre> getByFilmId(int id);

}
