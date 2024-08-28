package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Collection;

@RestController
@RequestMapping("/films")

public class FilmController {

    private final FilmService filmService;

    public FilmController(final FilmService filmService) {
        this.filmService = filmService;
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopLiked(@RequestParam(defaultValue = "10") final long count) {
        final Collection<Film> films = filmService.getTopFilmsByLikes(count);
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable final long id) {
        final Film film = filmService.getFilm(id).orElseThrow(
                () -> new NotFoundException(Film.class, id)
        );
        return film;
    }

    @PostMapping
    public Film createFilm(@RequestBody final Film newFilm) {
        return filmService.createFilm(newFilm);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        final Collection<Film> films = filmService.getFilms();
        return films;
    }

    @PutMapping
    public Film updateFilm(@RequestBody final Film updateFilm) {
        final Film film = filmService.updateFilm(updateFilm).orElseThrow(
                () -> new NotFoundException(Film.class, updateFilm.getId())
        );
        return film;
    }
}
