package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public Collection<User> getUsers() {
        log.info("The list of users has been received!");
        return users.values();
    }

    @Override
    public User getUserById(Integer id) {
        log.info("User received!");
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("The user has been successfully added!");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User data has been successfully updated!");
        return user;
    }

    @Override
    public boolean contains(Integer id) {
        return users.containsKey(id);
    }

    private Integer generateId() {
        return id++;
    }
}
