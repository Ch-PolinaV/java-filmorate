package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;

import java.util.List;

@Service
@Slf4j
public class RatingService {
    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<RatingMPA> findAll() {
        return ratingStorage.findAll();
    }

    public RatingMPA getRatingById(int id) {
        return ratingStorage.getMPAById(id);
    }
}
