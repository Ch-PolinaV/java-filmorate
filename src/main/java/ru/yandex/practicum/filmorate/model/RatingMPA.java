package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingMPA {
    private int id;
    private String name;
    private String description;

    public RatingMPA() {
    }
}
