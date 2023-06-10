package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Component
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingService ratingService;
    private final GenreService genreService;

    @Autowired
    public LikeStorage(JdbcTemplate jdbcTemplate, RatingService ratingService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingService = ratingService;
        this.genreService = genreService;
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
        String sql = "SELECT f.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID " +
                "FROM FILM f " +
                "LEFT JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getLong("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        ratingService.getRatingById(rs.getInt("rating_id")),
                        genreService.getFilmsGenres(rs.getLong("film_id"))),
                count);
    }
}
