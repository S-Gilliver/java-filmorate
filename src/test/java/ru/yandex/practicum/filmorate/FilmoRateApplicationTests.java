package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;

    @BeforeEach
    public void create() {
        Mpa mpa = new Mpa(4,"R");
        Genre genre = new Genre(4,"Триллер");
        Film film = new Film(1,
                "name",
                "description",
                LocalDate.of(1895, 12, 28),
                120,
                List.of(genre), mpa);
        User user = new User(1,
                "email",
                "login",
                "name",
                LocalDate.of(2005, 2, 22));
        filmStorage.addFilm(film);
        userStorage.addUser(user);
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}
