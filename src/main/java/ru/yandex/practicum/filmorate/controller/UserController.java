package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<User> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /users на получение всех пользователей");
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен POST-запрос к эндпоинту: /user на создание нового пользователя");

        if (users.containsKey(user.getId()) || user.getId() != 0) {
            log.info("Пользователь с id: {} уже существует", user.getId());
            throw new ValidationException("Пользователь с указанным id уже был добавлен ранее");
        } else {
            log.info("Добавлен новый пользователь: {}", user);
            setName(user);
            user.setId(createId());
            users.put(user.getId(), user);
            return user;
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Получен PUT-запрос к эндпоинту: /user на обновление или создание пользователя");

        if (!users.containsKey(user.getId())) {
            log.info("Несуществующий пользователь");
            throw new ValidationException("Несуществующий пользователь");
        } else {
            log.info("Пользователь с логином: {} обновлен", user.getId());
            setName(user);
            users.put(user.getId(), user);
            return user;
        }
    }

    private int createId() {
        return id++;
    }

    private void setName(User user) {
        if (user.getName().isBlank() || user.getName().isEmpty() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}