package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId()) || user.getId() != 0) {
            log.info("Пользователь с id: {} уже существует", user.getId());
            throw new ValidationException("Пользователь с указанным id уже был добавлен ранее");
        }
        log.info("Добавлен новый пользователь: {}", user);
        setName(user);
        user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь с id: {} обновлен", user.getId());
        setName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return null;
    }

    private long createId() {
        return id++;
    }

    private void setName(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
