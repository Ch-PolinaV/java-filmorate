package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private User user1;
    private User user2;
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user1 = new User("qwerty@ya.ru", "login", "name", LocalDate.of(1995, 5, 23));
        user2 = new User("zxcvb@ya.ru", "name", "", LocalDate.of(1995, 5, 23));
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
        Map<Integer, User> users = new HashMap<>();
        User firstUser = userController.create(user1);
        users.put(firstUser.getId(), firstUser);
        User secondUser = userController.create(user2);
        users.put(secondUser.getId(), secondUser);
        assertEquals(users, userController.findAll(), "Списки пользователей не совпадают");
    }
}
