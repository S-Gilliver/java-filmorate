package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
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
    public boolean deleteFilm(Film film) {
        films.remove(film.getId());
        return true;
    }

    @Override
    public boolean addLike(Integer id, Integer userId) {
        Film film = getFilmById(id);
        film.getLikeIds().add(userId);
        film.setRate(film.getRate() + 1);
        return true;
    }

    @Override
    public boolean deleteLike(Integer id, Integer userId) {
        Film film = getFilmById(id);
        film.getLikeIds().add(userId);
        film.setRate(film.getRate() - 1);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getFilms()
                .stream()
                .filter(film -> film.getLikeIds() != null)
                .sorted((t1, t2) -> t2.getLikeIds().size() - t1.getLikeIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }

    private Integer generateId() {
        return id++;
    }
}
