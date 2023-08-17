package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.time.LocalDate;

@Component
public class ServiceValidator {
    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Cinema hasn't been invented yet!");
        }
        if (film.getDuration() == null) {
            throw new ValidationException("An error in the duration of the movie!");
        }
    }

    public void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new BadRequestException("The login cannot contain spaces!");
        }
    }
}
