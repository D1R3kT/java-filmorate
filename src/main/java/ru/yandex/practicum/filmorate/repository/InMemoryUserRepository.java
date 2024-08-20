package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface InMemoryUserRepository {
    User createUser(User user);

    Collection<User> getAll();

    User getUser(Long id);

    User update(User newUser);
}