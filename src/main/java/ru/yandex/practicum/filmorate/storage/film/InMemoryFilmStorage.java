package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final Set<String> names = new HashSet<>();
    private Integer id = 1;

    @Override
    public Collection<Film> getFilms() {
        log.info("Got a list of movies!");
        return films.values();
    }

    @Override
    public Film getFilmById(Integer id) {
        log.info("The film was received!");
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        names.add(film.getName());
        log.info("The movie has been added successfully!");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        names.remove(getFilmById(film.getId()).getName());
        names.add(film.getName());
        films.put(film.getId(), film);
        log.info("The movie has been successfully updated!");
        return film;
    }

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }

    private Integer generateId() {
        return id++;
    }
}
