package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RequestMapping("/mpa")
@RestController
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<RatingMPA> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /mpa на получение всех рейтингов mpa");
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public RatingMPA getRatingById(@PathVariable int id) {
        log.debug("Получен GET-запрос к эндпоинту: /mpa/{id} на получение рейтинга mpa по id: {}", id);
        return ratingService.getRatingById(id);
    }
}