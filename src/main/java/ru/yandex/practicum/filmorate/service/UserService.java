package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
    }

    public User addUser(User user) {
        validateUser(user);
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userStorage.getUserById(user.getId())
                .orElseThrow(() -> new NotFoundException("The user with id "
                        + user.getId() + " does not exist!"));
        validateUser(user);
        if ((user.getName() == null) || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.deleteUser(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.addFriend(id, friendId);
        log.info(String.valueOf(userStorage.getUserById(id)));
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.deleteFriend(id, friendId);
        log.info(String.valueOf(userStorage.getUserById(id)));
    }

    public List<User> getFriends(Integer id) {
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        return userStorage.getFriends(id);
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("There is no such user!"));
        return  userStorage.getMutualFriends(id, otherId);
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
