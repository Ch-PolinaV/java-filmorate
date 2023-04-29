package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private final String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
}
