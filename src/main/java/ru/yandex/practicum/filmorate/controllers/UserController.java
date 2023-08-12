package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        validateUser(user);
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("The user has been successfully added!");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("The user with id " + user.getId() + " does not exist!");
        }
        validateUser(user);
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User data has been successfully updated!");
        return user;
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new BadRequestException("The login cannot contain spaces!");
        }
    }

    private int generateId() {
        return id++;
    }
}
