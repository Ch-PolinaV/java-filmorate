package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен GET-запрос к эндпоинту: /films на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Получен POST-запрос к эндпоинту: /film на добавление нового фильма");

        if (films.containsKey(film.getId())) {
            log.info("Фильм с id: {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с указанным id уже был добавлен ранее");
        } else if (isValid(film)) {
            film.setId(createId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: {}", film);
        }
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.debug("Получен PUT-запрос к эндпоинту: /film на обновление фильма");

        if (!films.containsKey(film.getId())) {
            log.info("Фильм с id: {} еще не добавлен", film.getName());
            throw new ValidationException("Фильм с указанным id еще не был добавлен");
        } else {
            if (isValid(film)) {
                films.put(film.getId(), film);
                log.info("Фильм с id: {} обновлен", film.getName());
            }
        }
        return film;
    }

    private int createId() {
        return id++;
    }

    private boolean isValid(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if ((film.getDescription().length()) > 200) {
            throw new ValidationException("Описание фильма больше 200 символов: " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Указанная дата релиза раньше дня рождения кино");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной: " + film.getDuration());
        }
        return true;
    }
}
