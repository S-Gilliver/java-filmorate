package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public Collection<User> getUsers() {
        final String sqlQuery = "SELECT * FROM users";

        log.info("The list of users has been received!");
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        try {
            final String sqlQuery = "SELECT * FROM users WHERE id = ?";
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, userMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User addUser(User user) {
        final String sqlQuery = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, generatedId);
        log.info("The user has been successfully added!");
        user.setId(Objects.requireNonNull(generatedId.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        final String sqlQuery = "UPDATE users " +
                "SET name = ?, login = ?, email = ?, birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        log.info("User data has been successfully updated!");
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        final String sqlQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("user successfully deleted");
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        final String sqlQuery = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, id, friendId);
            log.info("The user has been successfully added to friends");
        } catch (DataIntegrityViolationException ex) {
            log.error("The user is already a friend");
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        final String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("the user has been successfully removed from friends");
    }

    @Override
    public List<User> getFriends(Integer id) {
        final String sqlQuery = "SELECT u.id, u.email, u.name, u.login, u.birthday " +
                "FROM friends AS f LEFT JOIN users AS u " +
                "ON f.friend_id = u.id WHERE f.user_id = ?" +
                "ORDER BY u.id";
        log.info("Received a list of friends");
        return jdbcTemplate.query(sqlQuery, userMapper, id);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        final String sqlQuery = "SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "FROM friends as f " +
                "LEFT JOIN users AS u ON f.friend_id = u.id " +
                "WHERE f.user_id = ? " +
                "AND f.friend_id IN (SELECT friend_id FROM friends WHERE user_id = ?) ";
        log.info("Received a list of mutual friends");
        return jdbcTemplate.query(sqlQuery, userMapper, id, otherId);
    }
}
