package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {

    Mpa findAll();

    Mpa getById(int id);

    Mpa getByFilmId(int id);
}