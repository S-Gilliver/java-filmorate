package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    boolean deleteUser(User user);

    boolean addFriend(Integer id, Integer friendId);

    boolean deleteFriend(Integer id, Integer friendId);

    boolean contains(Integer id);
}
