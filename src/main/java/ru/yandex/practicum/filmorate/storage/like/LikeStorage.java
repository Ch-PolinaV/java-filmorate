package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            film.addLike();
        }
    }

    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        if (film != null && film.getLikes() > 0) {
            film.deleteLike();
        }
    }

    public Map<Long, Film> getPopularFilms(Integer count) {
        String filmSql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, mr.RATING_ID, mr.NAME AS RATING_NAME, mr.DESCRIPTION AS RATING_DESCRIPTION " +
                "FROM FILM f " +
                "LEFT JOIN MPA_RATING mr ON f.RATING_ID = mr.RATING_ID " +
                "LEFT JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(filmSql, FilmDbStorage::mapRowToFilm, count);
        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }
}
