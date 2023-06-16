package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RatingMPA> findAll() {
        String sql = "SELECT * FROM MPA_RATING";
        log.info("Выведен список всех рейтингов");

        return jdbcTemplate.query(sql, (rs, rowNum) -> new RatingMPA(
                        rs.getInt("rating_id"),
                        rs.getString("name"),
                        rs.getString("description"))).stream()
                .sorted(Comparator.comparing(RatingMPA::getId))
                .collect(Collectors.toList());
    }

    public RatingMPA getMPAById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM MPA_RATING WHERE RATING_ID = ?", id);

        if (mpaRows.next()) {
            return new RatingMPA(
                    mpaRows.getInt("rating_id"),
                    mpaRows.getString("name"),
                    mpaRows.getString("description")
            );
        }
        throw new NotFoundException("Рейтинг с идентификатором " + id + " не найден.");
    }
}