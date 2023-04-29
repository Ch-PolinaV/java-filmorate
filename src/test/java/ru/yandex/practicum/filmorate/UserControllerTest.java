package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        userController = new UserController();
        user1 = new User("qwerty@ya.ru", "login", "name", LocalDate.of(1995, 5, 23));
        user2 = new User("zxcvb@ya.ru", "name", "", LocalDate.of(1995, 5, 23));
    }

    @Test
    public void shouldAddNewUser() {
        User user = userController.create(user1);
        assertEquals(user1, user, "Не совпадают пользователи");
        System.out.println(user);
    }

    @Test
    public void shouldNotAddUserWithIncorrectEmail() {
        user1.setEmail("mail.ru");
        assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    public void shouldNotAddUserWithEmptyLogin() {
        user1.setLogin("  ");
        assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    public void shouldNotAddExistUser() {
        User user = userController.create(user1);
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void shouldAddUserWithEmptyName() {
        User user = userController.create(user2);
        assertEquals(user.getName(), user.getLogin(), "Логин не используется вместо имени");
    }

    @Test
    public void shouldNotAddUserWithBirthdayInFuture() {
        user1.setBirthday(LocalDate.of(2222, 12,3));
        assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    public void shouldReturnUsersList() {
        List<User> users = new ArrayList<>();
        User firstUser = userController.create(user1);
        users.add(firstUser);
        User secondUser = userController.create(user2);
        users.add(secondUser);
        assertEquals(users, userController.getUsers(), "Списки пользователей не совпадают");
    }

    @Test
    public void shouldUpdateUser() {
        userController.create(user1);
        user1.setName("newName");
        User user = userController.update(user1);
        assertEquals(user1.getName(), user.getName(), "Не совпадают имена пользователей");
        assertEquals("newName", user.getName(), "Не совпадают имена пользователей");
    }
}
