package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null || user == null) {
            log.info("Фильм и/или пользователь не найдены");
            throw new NotFoundException("Введены неверные данные фильма и/или пользователя");
        }
        if (film.getLikes().contains(userId)) {
            log.info("Пользователь с id: {} уже поставил лайк фильму с id: {}", userId, filmId);
            throw new ValidationException("Пользователь может поставить фильму лайк только 1 раз");
        }

        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null || user == null) {
            log.info("Фильм и/или пользователь не найдены");
            throw new NotFoundException("Введены неверные данные фильма и/или пользователя");
        }
        if (!film.getLikes().contains(userId)) {
            log.info("Пользователь с id: {} не ставил лайк фильму с id: {}", userId, filmId);
            throw new ValidationException("Лайк от пользователя с id: " + userId + "не найден");
        }

        log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getBestFilms(Long count) {
        if (count < 1) {
            log.info("Введено количество меньше 1");
            throw new ValidationException("Введенное число должно быть больше 0");
        }

        log.info("Получен список из {} наиболее популярных фильмов", count);
        return filmStorage.findAll().stream()
                .sorted(((o1, o2) -> o2.getLikes().size() - o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
