package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /films на получение всех фильмов");
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.debug("Получен GET-запрос к эндпоинту: /films/{id} на получение фильма по id: {}", id);
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") final Long count) {
        return filmService.getBestFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен POST-запрос к эндпоинту: /film на добавление нового фильма");
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен PUT-запрос к эндпоинту: /film на обновление или создание фильма");
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен GET-запрос к эндпоинту: /films/{}/like/{} на добавление лайка фильму", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен DELETE-запрос к эндпоинту: /films/{}/like/{} на удаление лайка фильму", id, userId);
        filmService.deleteLike(id, userId);
    }
}