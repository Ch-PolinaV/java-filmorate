package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<Film> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /films на получение всех фильмов");
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен POST-запрос к эндпоинту: /film на добавление нового фильма");

        if (films.containsKey(film.getId()) || film.getId() != 0) {
            log.info("Фильм с id: {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с указанным id уже был добавлен ранее");
        } else if (isValid(film)) {
            log.info("Добавлен новый фильм: {}", film);
            film.setId(createId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен PUT-запрос к эндпоинту: /film на обновление или создание фильма");
        isValid(film);
        if (!films.containsKey(film.getId())) {
            log.info("Несуществующий фильм");
            throw new ValidationException("Несуществующий фильм");
        } else {
            log.info("Фильм с id: {} обновлен", film.getId());
            films.put(film.getId(), film);
            return film;
        }
    }

    private int createId() {
        return id++;
    }

    private boolean isValid(Film film) {
        LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            log.info("Указанная дата релиза раньше дня рождения кино");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        } else {
            return true;
        }
    }
}
