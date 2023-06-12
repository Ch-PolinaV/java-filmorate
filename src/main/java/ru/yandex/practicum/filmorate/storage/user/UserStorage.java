package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User getUserById(long id);

    User create(User user);

    User update(User user);

    User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException;
}
