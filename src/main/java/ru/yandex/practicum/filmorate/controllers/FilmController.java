package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int id = 1;

    @GetMapping()
    public Collection<Film> getFilms() {
        return filmsMap.values();
    }

    @PostMapping()
    public Film addFilm(@RequestBody @Valid Film film) {
        filmValidator(film);
        film.setId(generateId());
        filmsMap.put(film.getId(), film);
        log.info("Фильм успешно добавлен!");
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) throws NotFoundException {
        if (!filmsMap.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не существует!");
        }
        filmValidator(film);
        filmsMap.put(film.getId(), film);
        log.info("Фильм успешно обновлен!");
        return film;
    }

    private void filmValidator(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Кино еще не изобрели!");
        }
    }

    private int generateId() {
        return id++;
    }
}
