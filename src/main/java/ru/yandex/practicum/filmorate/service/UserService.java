package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final ServiceValidator serviceValidator;

    public UserService(UserStorage userStorage, ServiceValidator serviceValidator) {
        this.userStorage = userStorage;
        this.serviceValidator = serviceValidator;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("There is no such user!");
        }
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        serviceValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.contains(user.getId())) {
            throw new NotFoundException("The user with id " + user.getId() + " does not exist!");
        }
        serviceValidator.validateUser(user);
        return userStorage.updateUser(user);
    }

    public String addFriend(int id, int friendId) {
        if (!userStorage.contains(id) || !userStorage.contains(friendId)) {
            throw new NotFoundException("One of the users is not registered!");
        }
        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
        log.info("Users added as friends!");
        return (userStorage.getUserById(id).getName()
                + " and " +  userStorage.getUserById(friendId).getName()
                + " now friends!");
    }

    public String deleteFriend(int id, int friendId) {
        if (!userStorage.contains(id) || !userStorage.contains(friendId)) {
            throw new NotFoundException("One of the users is not registered!");
        }
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
        log.info("Users removed from friends!");
        return (userStorage.getUserById(id).getName()
                + " and " + userStorage.getUserById(friendId).getName()
                + " not friends anymore!");
    }

    public List<User> getFriendList(int id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("The user is not registered!");
        }
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        User user = userStorage.getUserById(id);
        User other = userStorage.getUserById(otherId);
        return  user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
