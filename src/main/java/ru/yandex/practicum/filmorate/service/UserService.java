package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("There is no such user!");
        }
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.contains(user.getId())) {
            throw new NotFoundException("The user with id " + user.getId() + " does not exist!");
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public String addFriend(Integer id, Integer friendId) {
        if (!userStorage.contains(id) || !userStorage.contains(friendId)) {
            throw new NotFoundException("One of the users is not registered!");
        }
        userStorage.getUserById(id).getFriendIds().add(friendId);
        userStorage.getUserById(friendId).getFriendIds().add(id);
        log.info("Users added as friends!");
        return (userStorage.getUserById(id).getName()
                + " and " +  userStorage.getUserById(friendId).getName()
                + " now friends!");
    }

    public String deleteFriend(Integer id, Integer friendId) {
        if (!userStorage.contains(id) || !userStorage.contains(friendId)) {
            throw new NotFoundException("One of the users is not registered!");
        }
        userStorage.getUserById(id).getFriendIds().remove(friendId);
        userStorage.getUserById(friendId).getFriendIds().remove(id);
        log.info("Users removed from friends!");
        return (userStorage.getUserById(id).getName()
                + " and " + userStorage.getUserById(friendId).getName()
                + " not friends anymore!");
    }

    public List<User> getFriendList(Integer id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("The user is not registered!");
        }
        return userStorage.getUserById(id).getFriendIds().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        User user = userStorage.getUserById(id);
        User other = userStorage.getUserById(otherId);
        return  user.getFriendIds().stream()
                .filter(other.getFriendIds()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("The login cannot contain spaces!");
        }
        if (user.getLogin().isBlank()) {
            throw new ValidationException("User not have login!");
        }
    }
}
