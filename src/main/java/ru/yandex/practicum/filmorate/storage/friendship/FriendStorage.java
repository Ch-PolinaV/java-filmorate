package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.storage.user.UserDbStorage.userExists;

@Component
public class FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId) {
        if (!userExists(jdbcTemplate, userId) || !userExists(jdbcTemplate, friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        boolean status = friendExists(userId, friendId);

        String sqlInsert = "INSERT INTO friends (first_user_id, second_user_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsert, userId, friendId, status);

        if (status) {
            String sqlUpdateFriend = "UPDATE friends SET status = ? WHERE first_user_id = ? AND second_user_id = ?";
            jdbcTemplate.update(sqlUpdateFriend, true, friendId, userId);

            String sqlUpdateUser = "UPDATE friends SET status = ? WHERE first_user_id = ? AND second_user_id = ?";
            jdbcTemplate.update(sqlUpdateUser, true, userId, friendId);
        }
    }

    public void deleteFriend(long userId, long friendId) {
        if (!userExists(jdbcTemplate, userId) || !userExists(jdbcTemplate, friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        boolean status = friendExists(userId, friendId);

        String sql = "delete from friends " +
                "where first_user_id = ? and second_user_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

        if (status) {
            sql = "update friends set " +
                    "first_user_id = ?, second_user_id = ?, status = ? " +
                    "where first_user_id = ? and second_user_id = ?";
            jdbcTemplate.update(sql, friendId, userId, false, friendId, userId);
        }
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT * FROM FRIENDS f " +
                "JOIN USERS u ON f.SECOND_USER_ID = u.USER_ID " +
                "WHERE f.FIRST_USER_ID = " + userId;
        return jdbcTemplate.query(sql, UserDbStorage::mapRowToUser);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT * " +
                "FROM USERS u " +
                "WHERE u.USER_ID IN (" +
                "    SELECT f1.SECOND_USER_ID " +
                "    FROM FRIENDS f1 " +
                "    WHERE f1.FIRST_USER_ID = ?" +
                ") AND u.USER_ID IN (" +
                "    SELECT f2.SECOND_USER_ID" +
                "    FROM FRIENDS f2" +
                "    WHERE f2.FIRST_USER_ID = ?" +
                ")";
        return jdbcTemplate.query(sql, UserDbStorage::mapRowToUser, userId, otherId);
    }

    private boolean friendExists(long userId, long friendId) {
        String sql = "SELECT COUNT(*) > 0 FROM FRIENDS WHERE " +
                "((FIRST_USER_ID = ? AND SECOND_USER_ID = ?) OR " +
                "(FIRST_USER_ID = ? AND SECOND_USER_ID = ?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, friendId, friendId, userId));
    }
}
