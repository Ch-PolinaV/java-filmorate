package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotNull
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Long> likes;
    @NotNull
    private RatingMPA mpa;
    private Set<Genre> genres;

    public Film() {
    }

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Set<Long> likes, RatingMPA mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.mpa = mpa;
        this.genres = genres;
    }
}
