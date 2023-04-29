package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private Film film1;
    private Film film2;
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film1 = new Film("film1", "description", LocalDate.of(2001, 12, 10), 178);
        film2 = new Film("film3", "description", LocalDate.of(2002, 12, 5), 179);
    }

    @Test
    public void shouldAddNewFilm() {
        Film film = filmController.create(film1);
        assertEquals(film, film1, "Фильмы не совпадают");
    }

    @Test
    public void shouldNotAddExistFilm() {
        Film film = filmController.create(film1);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void shouldNotAddFilmWithEmptyName() {
        film1.setName("");
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    public void shouldNotAddFilmWithDescriptionLengthMore200Char() {
        film1.setDescription("Проверьте данные, которые приходят в запросе на добавление нового фильма " +
                "или пользователя. Эти данные должны соответствовать определённым критериям. Проверьте данные, " +
                "которые приходят в запросе на добавление нового фильма или пользователя. Эти данные должны " +
                "соответствовать определённым критериям.");
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    public void shouldNotAddFilmWithReleaseDateBeforeCinemaBirthday() {
        film1.setReleaseDate(LocalDate.of(999, 1,2));
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() {
        film1.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    public void shouldReturnFilmsList() {
        List<Film> films = new ArrayList<>();
        Film firstFilm = filmController.create(film1);
        films.add(firstFilm);
        Film secondFilm = filmController.create(film2);
        films.add(secondFilm);
        assertEquals(films, filmController.getFilms(), "Списки фильмов не совпадают");
    }

    @Test
    public void shouldUpdateFilm() {
        filmController.create(film1);
        film1.setName("newName");
        Film film = filmController.update(film1);
        assertEquals(film1.getName(), film.getName(), "Не совпадают названия фильмов");
        assertEquals("newName", film.getName(), "Не совпадают названия фильмов");
    }
}
