package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    boolean contains(Integer id);
}
