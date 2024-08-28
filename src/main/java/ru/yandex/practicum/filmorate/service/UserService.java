package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.excepion.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class UserService {

    private final JdbcUserRepository jdbcUserRepository;

    public UserService(JdbcUserRepository jdbcUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
    }

    public Collection<User> getAllUsers() {
        return jdbcUserRepository.findAll();
    }

    public Optional<User> getUser(final Long userId) {
        return jdbcUserRepository.findById(userId);
    }

    public Optional<User> createUser(final User user) {
        Objects.requireNonNull(user, "user must not be null");
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must not be empty");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "login must not be empty");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be empty");
        }
        final User newUser = jdbcUserRepository.save(user);
        log.info("Created new user: {}", newUser);
        return Optional.of(newUser);
    }

    public Optional<User> updateUser(final User user) {
        Objects.requireNonNull(user, "user must not be null");
        if (jdbcUserRepository.findById(user.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        final Optional<User> existingUser = jdbcUserRepository.update(user);
        existingUser.ifPresent(u -> log.info("Updated user: {}", u));
        return existingUser;
    }

    public void addFriend(final Long userId, final Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("id не должны совпадать");
        }
        if (jdbcUserRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        if (jdbcUserRepository.findById(friendId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "friend not found");
        }
        jdbcUserRepository.addFriend(userId, friendId);
    }

    public void removeFriend(final Long userId, final Long friendId) {

        if (jdbcUserRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        if (jdbcUserRepository.findById(friendId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "friend not found");
        }
        jdbcUserRepository.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(final Long userId) {
        if (jdbcUserRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        return jdbcUserRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriends(final Long userId, final Long friendId) {
        return jdbcUserRepository.findCommonFriends(userId, friendId);
    }
}
