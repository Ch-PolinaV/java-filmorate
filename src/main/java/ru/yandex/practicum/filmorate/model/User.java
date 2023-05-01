package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать формату электронного адреса")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "\\S*$")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
        if (name == null || name.isBlank()) {
            this.name = login;
        }
    }
}
