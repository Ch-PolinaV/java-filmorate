package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final RatingService ratingService;
    private final GenreService genreService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(RatingService ratingService, GenreService genreService, JdbcTemplate jdbcTemplate) {
        this.ratingService = ratingService;
        this.genreService = genreService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM FILM";
        log.info("Выведен список всех фильмов");

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                ratingService.getRatingById(rs.getInt("rating_id")),
                genreService.getFilmsGenres(rs.getLong("film_id")))
        );
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", id);

        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    ratingService.getRatingById(filmRows.getInt("rating_id")),
                    genreService.getFilmsGenres(id)
            );

            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        }
        throw new NotFoundException("Фильм с идентификатором " + id + " не найден.");
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(InMemoryFilmStorage.CINEMA_BIRTHDAY)) {
            log.info("Указанная дата релиза раньше дня рождения кино");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).longValue());
        film.setMpa(ratingService.getRatingById(film.getMpa().getId()));

        if (film.getGenres() != null) {
            genreService.addFilmGenre(film);
        }

        log.info("Добавлен фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (getFilmById(film.getId()) == null) {
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        film.setMpa(ratingService.getRatingById(film.getMpa().getId()));

        if (film.getGenres() != null) {
            Collection<Genre> genres = film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(new LinkedHashSet<>(genres));

            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
        }
        genreService.addFilmGenre(film);
        log.info("Фильм с id: {} обновлен", film.getId());
        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_id", film.getMpa().getId());
        return values;
    }
}
