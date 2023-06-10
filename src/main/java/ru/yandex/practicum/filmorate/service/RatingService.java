package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingService {
    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<RatingMPA> findAll() {
        return ratingStorage.findAll().stream()
                .sorted(Comparator.comparing(RatingMPA::getId))
                .collect(Collectors.toList());
    }

    public RatingMPA getRatingById(int id) {
        return ratingStorage.getMPAById(id);
    }
}
