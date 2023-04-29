package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private Film film1;
    private Film film2;
    private Film film3;
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film1 = new Film("film1", "description", LocalDate.of(2001, 12, 10), 178);
        film2 = new Film("film3", "description", LocalDate.of(2002, 12, 5), 179);
        film3 = new Film("film2", "description", LocalDate.of(1002, 12, 5), 179);
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
    public void shouldReturnFilmsList() {
        Map<Integer, Film> films = new HashMap<>();
        Film firstFilm = filmController.create(film1);
        films.put(firstFilm.getId(), firstFilm);
        Film secondFilm = filmController.create(film2);
        films.put(secondFilm.getId(), secondFilm);
        assertEquals(films, filmController.findAll(), "Списки фильмов не совпадают");
    }

    @Test
    public void shouldNotAddNewFilmWithIncorrectReleaseDate() {
        assertThrows(ValidationException.class, () -> filmController.create(film3));
    }
}
