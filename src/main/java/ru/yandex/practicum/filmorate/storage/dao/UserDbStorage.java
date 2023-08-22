package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(Integer id) {
        String sqlUser = "select * from USERS where id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlUser, (rs, rowNum) -> makeUser(rs), id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с идентификатором " +
                    id + " не зарегистрирован!");
        }
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        String sqlAllUsers = "select * from USERS";
        return jdbcTemplate.query(sqlAllUsers, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into USERS " +
                "(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));

            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (user.getFriendIds() != null) {
            for (Integer friendId : user.getFriendIds()) {
                addFriend(user.getId(), friendId);
            }
        }
        return getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        String sqlUser = "update USERS set " +
                "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "where ID = ?";
        jdbcTemplate.update(sqlUser,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return getUserById(user.getId());
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlQuery = "delete from USERS where ID = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("ID");
        return new User(
                userId,
                resultSet.getString("EMAIL"),
                resultSet.getString("LOGIN"),
                resultSet.getString("NAME"),
                Objects.requireNonNull(resultSet.getDate("BirthDay")).toLocalDate(),
                (Set<Integer>) getUserFriends(userId));
    }

    private List<Integer> getUserFriends(int userId) {
        String sqlGetFriends = "select USER_FRIENDS_ID from USER_FRIENDS where USER_ID = ?";
        return jdbcTemplate.queryForList(sqlGetFriends, Integer.class, userId);
    }

    @Override
    public boolean addFriend(Integer id, Integer friendId) {
        boolean friendAccepted;
        String sqlGetReversFriend = "select * from USER_FRIENDS " +
                "where USER_ID = ? and USER_FRIENDS_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlGetReversFriend, friendId, id);
        friendAccepted = sqlRowSet.next();
        String sqlSetFriend = "insert into USER_FRIENDS (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?,?,?)";
        jdbcTemplate.update(sqlSetFriend, id, friendId, friendAccepted);
        if (friendAccepted) {
            String sqlSetStatus = "update USER_FRIENDS set STATUS = true " +
                    "where USER_ID = ? and FRIEND_ID = ?";
            jdbcTemplate.update(sqlSetStatus, friendId, id);
        }
        return true;
    }

    @Override
    public boolean deleteFriend(Integer id, Integer friendId) {
        String sqlDeleteFriend = "delete from USER_FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlDeleteFriend, id, friendId);
        String sqlSetStatus = "update USER_FRIENDS set STATUS = false " +
                "where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlSetStatus, friendId, id);
        return true;
    }

    @Override
    public boolean contains(Integer id) {
        return false;
    }

}

