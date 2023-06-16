package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        log.info("Выведен список всех пользователей");

        return jdbcTemplate.query(sql, UserDbStorage::mapRowToUser);
    }

    @Override
    public User getUserById(long id) {
        if (!userExists(jdbcTemplate, id)) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        log.info("Найден пользователь: id = {}", id);
        return jdbcTemplate.queryForObject(sql, UserDbStorage::mapRowToUser, id);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue());

        log.info("Добавлен пользователь: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        if (!userExists(jdbcTemplate, user.getId())) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь с id: {} обновлен", user.getId());
        return user;
    }

    public static boolean userExists(JdbcTemplate jdbcTemplate, long userId) {
        String sqlQuery = "SELECT (COUNT(*) > 0) FROM users WHERE user_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, userId));
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }
}
