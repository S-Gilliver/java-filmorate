package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Collection<Film> getFilms() {
        log.info("Got a list of movies!");
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        log.info("The film was received!");
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("The movie has been added successfully!");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("The movie has been successfully updated!");
        return film;
    }

    @Override
    public boolean contains(int id) {
        return films.containsKey(id);
    }

    private int generateId() {
        return id++;
    }
}
