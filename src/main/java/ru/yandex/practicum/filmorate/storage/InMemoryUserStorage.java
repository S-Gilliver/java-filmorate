package ru.yandex.practicum.filmorate.storage;

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
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("The user has been successfully added!");
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("User data has been successfully updated!");
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        users.remove(user.getId());
        return true;
    }

    @Override
    public boolean addFriend(Integer id, Integer friendId) {
        getUserById(id).getFriendIds().add(friendId);
        getUserById(friendId).getFriendIds().add(id);
        return true;
    }

    @Override
    public boolean deleteFriend(Integer id, Integer friendId) {
        getUserById(id).getFriendIds().remove(friendId);
        getUserById(friendId).getFriendIds().remove(id);
        return true;
    }

    @Override
    public boolean contains(Integer id) {
        return users.containsKey(id);
    }

    private Integer generateId() {
        return id++;
    }
}
