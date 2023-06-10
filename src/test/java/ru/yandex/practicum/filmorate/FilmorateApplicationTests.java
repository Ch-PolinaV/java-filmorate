package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final GenreStorage genreStorage;
	private final RatingStorage ratingStorage;
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private User user1;
	private User user2;
	private Film film1;
	private Film film2;
	private Film film3;


	@BeforeEach
	public void beforeEach() {
		user1 = new User(0, "qwerty@ya.ru", "login", "name", LocalDate.of(1995, 5, 23));
		user2 = new User(0, "zxcvb@ya.ru", "name", "", LocalDate.of(1995, 5, 23));
		Genre genre1 = new Genre(1, "Комедия");
		Genre genre3 = new Genre(3, "Мультфильм");
		Genre genre4 = new Genre(4, "Триллер");
		RatingMPA mpa = new RatingMPA(1, "G", "у фильма нет возрастных ограничений");
		film1 = new Film(0, "film1", "description", LocalDate.of(2001, 12, 10), 178, mpa, Set.of(genre1, genre3));
		film2 = new Film(0, "film3", "description", LocalDate.of(2002, 12, 5), 179, mpa, Set.of(genre1, genre4));
		film3 = new Film(0, "film2", "description", LocalDate.of(2003, 12, 5), 179, mpa, Set.of(genre3));
	}

	@Test
	public void testCreateAndFindUserById() {
		userStorage.create(user1);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	public void testFindAllUsers() {
		userStorage.create(user1);
		userStorage.create(user2);
		Optional<List<User>> optionalList = Optional.ofNullable(userStorage.findAll());

		int size = optionalList
				.map(List::size)
				.orElse(0);
		assertEquals(2, size);
	}

	@Test
	public void testUpdateUser() {
		userStorage.create(user1);
		user1.setName("NewName");
		userStorage.update(user1);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(user1.getId()));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "NewName")
				);
	}

	@Test
	public void testCreateAndFindFilmById() {
		filmStorage.create(film1);
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	public void testFindAllFilms() {
		filmStorage.create(film1);
		filmStorage.create(film2);
		filmStorage.create(film3);
		Optional<List<Film>> optionalList = Optional.ofNullable(filmStorage.findAll());

		int size = optionalList
				.map(List::size)
				.orElse(0);
		assertEquals(3, size);
	}

	@Test
	public void testUpdateFilm() {
		filmStorage.create(film1);

		film1.setName("NewFilm");

		filmStorage.update(film1);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(film1.getId()));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("name", "NewFilm")
						);
	}

	@Test
	public void testFindGenreById() {
		Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getGenreById(1));

		assertThat(genreOptional)
				.isPresent()
				.hasValueSatisfying(genre ->
						assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testFindAllGenres() {
		Optional<List<Genre>> optionalList = Optional.ofNullable(genreStorage.findAll());

		int size = optionalList
				.map(List::size)
				.orElse(0);
		assertEquals(6, size);
	}

	@Test
	public void testFindRatingById() {
		Optional<RatingMPA> ratingMPAOptional = Optional.ofNullable(ratingStorage.getMPAById(1));

		assertThat(ratingMPAOptional)
				.isPresent()
				.hasValueSatisfying(ratingMPA ->
						assertThat(ratingMPA).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testFindAllRatings() {
		Optional<List<RatingMPA>> optionalList = Optional.ofNullable(ratingStorage.findAll());

		int size = optionalList
				.map(List::size)
				.orElse(0);
		assertEquals(5, size);
	}
}