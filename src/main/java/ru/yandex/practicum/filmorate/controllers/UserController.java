package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> usersMap = new HashMap<>();

    private int id;

    @GetMapping
    public Collection<User> getUsers() {
        return usersMap.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        userValidator(user);
        user.setId(generateId());
        usersMap.put(user.getId(), user);
        log.info("Пользователь успешно добавлен!");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (!usersMap.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не существует!");
        }
        userValidator(user);
        usersMap.put(user.getId(), user);
        log.info("Данные о пользователе успешно обновлены!");
        return user;
    }

    private void userValidator(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            throw new BadRequestException("Логин не может содержать пробелы!");
        }
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private int generateId() {
        return id++;
    }
}
