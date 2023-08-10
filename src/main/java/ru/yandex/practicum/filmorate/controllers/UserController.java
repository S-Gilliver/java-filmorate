package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ResponseBody
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> usersMap = new HashMap<>();

    private int id;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        if (user.getLogin().contains(" ")) {
            throw new BadRequestException("Логин не может содержать пробелы!");
        }
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        usersMap.put(user.getId(), user);
        log.info("Пользователь успешно добавлен!");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (user.getLogin().contains(" ")) {
            throw new BadRequestException("Логин не может содержать пробелы!");
        }
        if (!usersMap.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не существует!");
        }
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        usersMap.put(user.getId(), user);
        log.info("Данные о пользователе успешно обновлены!");
        return user;
    }

    private int generateId() {
        return id++;
    }
}
