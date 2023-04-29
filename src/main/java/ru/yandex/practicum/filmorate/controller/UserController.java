package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен GET-запрос к эндпоинту: /users на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("Получен POST-запрос к эндпоинту: /user на создание нового пользователя");

        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id: {} уже существует", user.getId());
            throw new ValidationException("Пользователь с указанным id уже был добавлен ранее");
        } else if (isValid(user)) {
            user.setId(createId());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: {}", user);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.debug("Получен PUT-запрос к эндпоинту: /user на обновление пользователя");

        if (users.containsKey(user.getId()) && isValid(user)) {
            users.put(user.getId(), user);
            log.info("Пользователь с id: {} обновлен", user.getId());
            return user;
        } else {
            log.info("Пользователь с id: {} еще не существует", user.getId());
            throw new ValidationException("Пользователь с указанным id еще не был добавлен");
        }
    }

    private int createId() {
        return id++;
    }

    private boolean isValid(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Некорректный e-mail пользователя: " + user.getEmail());
        }
        if ((user.getLogin().isBlank()) || (user.getLogin().contains(" "))) {
            throw new ValidationException("Некорректный логин пользователя: " + user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения пользователя: " + user.getBirthday());
        }
        if (user.getName().isBlank()) {
            String newName = user.getLogin();
            user.setName(newName);
        }
        return true;
    }
}