package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private User user1;
    private User user2;
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
        user1 = new User(0, "qwerty@ya.ru", "login", "name", LocalDate.of(1995, 5, 23), null);
        user2 = new User(0, "zxcvb@ya.ru", "name", "", LocalDate.of(1995, 5, 23), null);
    }

    @Test
    public void shouldAddNewUser() {
        User user = userController.create(user1);
        assertEquals(user1, user, "Не совпадают пользователи");
    }

    @Test
    public void shouldNotAddExistUser() {
        User sameUser = userController.create(user1);
        assertThrows(ValidationException.class, () -> userController.create(sameUser));
    }

    @Test
    public void shouldAddUserWithEmptyName() {
        User user = userController.create(user2);
        assertEquals(user.getName(), user.getLogin(), "Логин не используется вместо имени");
    }

    @Test
    public void shouldReturnUsersList() {
        List<User> users = new ArrayList<>();
        User firstUser = userController.create(user1);
        User secondUser = userController.create(user2);
        users.add(firstUser);
        users.add(secondUser);
        assertEquals(users, userController.findAll(), "Списки пользователей не совпадают");
    }
}
