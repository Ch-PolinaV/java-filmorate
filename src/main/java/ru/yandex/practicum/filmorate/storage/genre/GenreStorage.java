package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAll() {
        String sql = "SELECT * FROM GENRE";
        log.info("Выведен список всех жанров");

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        );
    }

    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE GENRE_ID = ?", id);

        if (genreRows.first()) {
            return new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")
            );
        }
        throw new NotFoundException("Жанр с идентификатором " + id + " не найден.");
    }

    public Set<Genre> getFilmsGenres(long filmId) {
        String sql = "SELECT fg.GENRE_ID, NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?;";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")), filmId)
        );
    }

    public void updateFilmGenre(Film film) {
        deleteFilmGenre(film);
        addFilmGenre(film);
    }

    public String getFilmGenresQuery(List<Long> filmIds) {
        String filmIdList = filmIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "(", ")"));
        return "SELECT fg.FILM_ID, g.GENRE_ID, g.NAME AS GENRE_NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID IN " + filmIdList;
    }

    private void addFilmGenre(Film film) {
        if (film.getGenres() != null) {
            List<Object[]> genres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                genres.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)", genres);
        }
    }

    private void deleteFilmGenre(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", film.getId());
    }
}
