package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NonNull
    @NotEmpty(message = "Название не может быть пустым")
    private String name;
    @NonNull
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}
