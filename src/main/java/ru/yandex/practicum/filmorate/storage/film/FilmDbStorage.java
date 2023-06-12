package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(RatingStorage ratingStorage, GenreStorage genreStorage, JdbcTemplate jdbcTemplate) {
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String filmSql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, mr.RATING_ID, mr.NAME AS RATING_NAME, mr.DESCRIPTION AS RATING_DESCRIPTION " +
                "FROM FILM f " +
                "LEFT JOIN MPA_RATING mr ON f.RATING_ID = mr.RATING_ID";

        String genreSql = "SELECT fg.FILM_ID, g.GENRE_ID, g.NAME AS GENRE_NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID IN (SELECT FILM_ID FROM FILM)";

        Map<Long, Film> filmMap = new HashMap<>();

        jdbcTemplate.query(filmSql, rs -> {
            long filmId = rs.getLong("FILM_ID");
            Film film = filmMap.getOrDefault(filmId, new Film());
            if (film.getId() == 0) {
                film.setId(filmId);
                film.setName(rs.getString("NAME"));
                film.setDescription(rs.getString("DESCRIPTION"));
                film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
                film.setDuration(rs.getInt("DURATION"));
                film.setLikes(new HashSet<>());
                RatingMPA mpaRating = new RatingMPA();
                mpaRating.setId(rs.getInt("RATING_ID"));
                mpaRating.setName(rs.getString("RATING_NAME"));
                mpaRating.setDescription(rs.getString("RATING_DESCRIPTION"));
                film.setMpa(mpaRating);
                film.setGenres(new HashSet<>());
                filmMap.put(filmId, film);
            }
        });

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

    @Override
    public Film getFilmById(long id) {
        if (!filmExists(id)) {
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        String filmSql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, mr.RATING_ID, mr.NAME AS RATING_NAME, mr.DESCRIPTION AS RATING_DESCRIPTION " +
                "FROM FILM f " +
                "LEFT JOIN MPA_RATING mr ON f.RATING_ID = mr.RATING_ID " +
                "WHERE f.FILM_ID = ?";

        String genreSql = "SELECT g.GENRE_ID, g.NAME AS GENRE_NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";

        Film film = jdbcTemplate.queryForObject(filmSql, this::mapRowToFilm, id);
        if (film != null) {
            film.setGenres(new HashSet<>());
            jdbcTemplate.query(genreSql, rs -> {
                int genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                film.getGenres().add(genre);
            }, id);
        }

        return film;
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
        film.setMpa(ratingStorage.getMPAById(film.getMpa().getId()));

        if (film.getGenres() != null) {
            genreStorage.updateFilmGenre(film);
        }

        log.info("Добавлен фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!filmExists(film.getId())) {
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
        film.setMpa(ratingStorage.getMPAById(film.getMpa().getId()));

        if (film.getGenres() != null) {
            Collection<Genre> genres = film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(new LinkedHashSet<>(genres));

            for (Genre genre : film.getGenres()) {
                genre.setName(genreStorage.getGenreById(genre.getId()).getName());
            }
        }
        genreStorage.updateFilmGenre(film);
        log.info("Фильм с id: {} обновлен", film.getId());
        return film;
    }

    private boolean filmExists(long filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM film WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        return Optional.ofNullable(count).map(c -> c > 0).orElse(false);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        RatingMPA mpa = new RatingMPA(rs.getInt("RATING_ID"), rs.getString("RATING_NAME"), rs.getString("RATING_DESCRIPTION"));

        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(mpa)
                .build();
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
