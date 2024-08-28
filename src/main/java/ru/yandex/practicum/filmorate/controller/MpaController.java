package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(final MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable final long id) {
        final Mpa mpa = mpaService.getMpa(id).orElseThrow(
                () -> new NotFoundException(Mpa.class, id)
        );
        return mpa;
    }

    @GetMapping
    public Collection<Mpa> getMpas() {
        final Collection<Mpa> mpas = mpaService.getMpas();
        return mpas;
    }
}
