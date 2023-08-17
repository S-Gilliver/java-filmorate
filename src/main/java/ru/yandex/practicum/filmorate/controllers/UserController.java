package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public User addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public String addFriend(@PathVariable(value = "id") Integer id,
                            @PathVariable(value = "friendId") Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFriend(@PathVariable(value = "id") Integer id,
                               @PathVariable(value = "friendId") Integer friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendList(@PathVariable(value = "id") Integer id) {
        return userService.getFriendList(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getMutualFriends(@PathVariable(value = "id") Integer id,
                                        @PathVariable(value = "otherId") Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}
