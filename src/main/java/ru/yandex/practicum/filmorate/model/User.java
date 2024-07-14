package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
