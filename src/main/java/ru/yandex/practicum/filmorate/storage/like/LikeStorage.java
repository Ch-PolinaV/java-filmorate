package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Autowired
    public LikeStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findAll());
        popularFilms.sort(Comparator.comparingInt(f -> f.getLikes().size()));
        Collections.reverse(popularFilms);

        if (count != null && count < popularFilms.size()) {
            popularFilms = popularFilms.subList(0, count);
        }

        return popularFilms;
    }
}
