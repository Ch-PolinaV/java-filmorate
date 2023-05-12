package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private long id = 1;

    @Override
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public Film create(@Valid @RequestBody Film film) {
        isValid(film);

        if (films.containsKey(film.getId()) || film.getId() != 0) {
            log.info("Фильм с id: {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с указанным id уже был добавлен ранее");
        }
        log.info("Добавлен новый фильм: {}", film);
        film.setId(createId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(@Valid @RequestBody Film film) {
        isValid(film);
        if (!films.containsKey(film.getId())) {
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Фильм с id: {} обновлен", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    private long createId() {
        return id++;
    }

    private void isValid(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.info("Указанная дата релиза раньше дня рождения кино");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}