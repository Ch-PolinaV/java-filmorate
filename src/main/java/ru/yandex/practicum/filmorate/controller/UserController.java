package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private final Map<String, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Map<String, User> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /users на получение всех пользователей");
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен POST-запрос к эндпоинту: /user на создание нового пользователя");

        if (users.containsKey(user.getLogin())) {
            log.info("Пользователь с логином: {} уже существует", user.getLogin());
            throw new ValidationException("Пользователь с указанным логином уже был добавлен ранее");
        } else {
            checkName(user);
            user.setId(createId());
            users.put(user.getLogin(), user);
            log.info("Добавлен новый пользователь: {}", user);
            return user;
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Получен PUT-запрос к эндпоинту: /user на обновление пользователя");
        checkName(user);

        if (users.containsKey(user.getLogin())) {
            users.put(user.getLogin(), user);
            log.info("Пользователь с логином: {} обновлен", user.getLogin());
            return user;
        } else {
            log.info("Пользователь с логином: {} еще не существует", user.getLogin());
            throw new ValidationException("Пользователь с указанным логином еще не был добавлен");
        }
    }

    private int createId() {
        return id++;
    }

    private void checkName(User user) {
        if (user.getName().isBlank()) {
            String newName = user.getLogin();
            user.setName(newName);
        }
    }
}