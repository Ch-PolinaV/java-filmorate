package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
public class FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FriendStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        User friend = userStorage.getUserById(friendId);
        boolean status = friend.getFriends().contains(userId);

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
        User friend = userStorage.getUserById(friendId);

        String sql = "delete from friends " +
                "where first_user_id = ? and second_user_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        if (friend.getFriends().contains(userId)) {
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
        return jdbcTemplate.query(sql, userStorage::mapRowToUser);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT USER_ID , EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM FRIENDS f1 " +
                "JOIN FRIENDS f2 ON f2.FIRST_USER_ID = " + userId + " AND f2.SECOND_USER_ID = f1.SECOND_USER_ID " +
                "JOIN USERS u ON u.USER_ID = f1.SECOND_USER_ID " +
                "WHERE f1.FIRST_USER_ID = " + otherId;
        return jdbcTemplate.query(sql, userStorage::mapRowToUser);
    }
}
