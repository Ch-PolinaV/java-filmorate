//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.exeption.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Genre;
//import ru.yandex.practicum.filmorate.model.RatingMPA;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
//import ru.yandex.practicum.filmorate.storage.user.UserStorage;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//public class FilmControllerTest {
//    private Film film1;
//    private Film film2;
//    private Film film3;
//    private FilmController filmController;
//
//    @BeforeEach
//    public void beforeEach() {
//        FilmStorage filmStorage = new InMemoryFilmStorage();
//        UserStorage userStorage = new InMemoryUserStorage();
//        FilmService filmService = new FilmService(filmStorage, userStorage, likeStorage);
//        filmController = new FilmController(filmService);
//        Genre genre1 = new Genre(1, "Комедия");
//        Genre genre3 = new Genre(3, "Мультфильм");
//        Genre genre4 = new Genre(4, "Триллер");
//        RatingMPA mpa = new RatingMPA(1, "G", "у фильма нет возрастных ограничений");
//        film1 = new Film(0, "film1", "description", LocalDate.of(2001, 12, 10), 178, mpa, List.of(genre1, genre3));
//        film2 = new Film(0, "film3", "description", LocalDate.of(2002, 12, 5), 179, mpa, List.of(genre1, genre4));
//        film3 = new Film(0, "film2", "description", LocalDate.of(1002, 12, 5), 179, mpa, List.of(genre3));
//    }
//
//    @Test
//    public void shouldAddNewFilm() {
//        Film film = filmController.create(film1);
//        assertEquals(film, film1, "Фильмы не совпадают");
//    }
//
//    @Test
//    public void shouldNotAddExistFilm() {
//        Film film = filmController.create(film1);
//        assertThrows(ValidationException.class, () -> filmController.create(film));
//    }
//
//    @Test
//    public void shouldReturnFilmsList() {
//        List<Film> films = new ArrayList<>();
//        Film firstFilm = filmController.create(film1);
//        Film secondFilm = filmController.create(film2);
//        films.add(firstFilm);
//        films.add(secondFilm);
//        assertEquals(films, filmController.findAll(), "Списки фильмов не совпадают");
//    }
//
//    @Test
//    public void shouldNotAddNewFilmWithIncorrectReleaseDate() {
//        assertThrows(ValidationException.class, () -> filmController.create(film3));
//    }
//}
