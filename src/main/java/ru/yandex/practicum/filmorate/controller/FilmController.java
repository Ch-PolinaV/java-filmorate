package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private final Map<String, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Map<String, Film> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /films на получение всех фильмов");
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен POST-запрос к эндпоинту: /film на добавление нового фильма");

        if (films.containsKey(film.getName())) {
            log.info("Фильм с именем: {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с указанным названием уже был добавлен ранее");
        } else if (isValid(film)) {
            film.setId(createId());
            films.put(film.getName(), film);
            log.info("Добавлен новый фильм: {}", film);
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен PUT-запрос к эндпоинту: /film на обновление фильма");

        if (!films.containsKey(film.getName())) {
            log.info("Фильм с именем: {} еще не добавлен", film.getName());
            throw new ValidationException("Фильм с указанным названием еще не был добавлен");
        } else {
            if (isValid(film)) {
                films.put(film.getName(), film);
                log.info("Фильм с именем: {} обновлен", film.getName());

            }
        }
        return film;
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
