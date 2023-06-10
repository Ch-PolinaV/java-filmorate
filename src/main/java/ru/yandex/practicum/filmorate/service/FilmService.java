package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        try {
            likeStorage.deleteLike(filmId, userId);
            log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, filmId);
        } catch (Exception e) {
            log.info("Лайк пользователя id={} не найден", userId);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count < 1) {
            log.info("Введено количество меньше 1");
            throw new ValidationException("Введенное число должно быть больше 0");
        }

        log.info("Получен список из {} наиболее популярных фильмов", count);
        return likeStorage.getPopularFilms(count);
    }
}