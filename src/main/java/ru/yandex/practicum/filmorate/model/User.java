package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

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
    private final String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "\\S*$")
    private final String login;
    @NonNull
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
}
