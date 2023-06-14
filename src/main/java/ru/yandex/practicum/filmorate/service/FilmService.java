package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, LikeStorage likeStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = new GenreStorage(jdbcTemplate);
    }

    public List<Film> findAll() {
        Map<Long, Film> filmMap = new HashMap<>(filmStorage.findAll());
        List<Long> ids = new ArrayList<>(filmMap.keySet());
        String genreSql = genreStorage.getFilmGenresQuery(ids);

        jdbcTemplate.query(genreSql, rs -> {
            long filmId = rs.getLong("FILM_ID");
            Film film = filmMap.get(filmId);
            if (film != null) {
                int genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                film.getGenres().add(genre);
            }
        });
        return new ArrayList<>(filmMap.values());
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            List<Long> idList = new ArrayList<>(List.of(film.getId()));
            String genreSql = genreStorage.getFilmGenresQuery(idList);
            film.setGenres(new HashSet<>());
            jdbcTemplate.query(genreSql, rs -> {
                int genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                film.getGenres().add(genre);
            });
        }
        return film;
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
        Map<Long, Film> filmMap = new HashMap<>(likeStorage.getPopularFilms(count));
        List<Long> ids = new ArrayList<>(filmMap.keySet());
        String genreSql = genreStorage.getFilmGenresQuery(ids);

        jdbcTemplate.query(genreSql, rs -> {
            long filmId = rs.getLong("FILM_ID");
            Film film = filmMap.get(filmId);
            if (film != null) {
                int genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                film.getGenres().add(genre);
            }
        });
        return new ArrayList<>(filmMap.values());
    }
}