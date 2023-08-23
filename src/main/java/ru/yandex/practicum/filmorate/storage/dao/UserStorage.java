package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    Optional<User> getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

    void addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getMutualFriends(Integer id, Integer otherId);
}
