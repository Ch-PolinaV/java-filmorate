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
        boolean status = false;
        if (friend.getFriends().contains(userId)) {
            status = true;
            String sql = "update friends set " +
                    "first_user_id = ?, second_user_id = ?, status = ? " +
                    "where first_user_id = ? and second_user_id = ?";
            jdbcTemplate.update(sql, friendId, userId, true, friendId, userId);
        }
        String sql = "insert into friends (first_user_id, second_user_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, status);
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
                "WHERE f.FIRST_USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                        rs.getLong("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate()),
                userId
        );
    }
}
