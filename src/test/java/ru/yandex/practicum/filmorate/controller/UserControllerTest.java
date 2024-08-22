package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;


public class UserControllerTest {

    @Test
    @DisplayName("Name")
    void shouldCreateUserWithoutName() {
        UserRepository userService = new InMemoryUserRepository();
        User user = new User();
        user.setEmail("email@mail.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2000, 10, 10));
        userService.createUser(user);

        Assertions.assertEquals("Login", user.getName());
    }

    @Test
    @DisplayName("Email")
    void shouldCreateUserWithWrongEmail() {
        UserRepository userService = new InMemoryUserRepository();
        User user = new User();
        user.setEmail("emailmail.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2000, 10, 10));

        try {
            userService.createUser(user);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                            " \"электронная почта не может быть пустой и должна содержать символ @\"",
                    exp.getMessage());
        }
    }

    @Test
    @DisplayName("Login")
    void shouldCreateUserWithoutLogin() {
        UserRepository userService = new InMemoryUserRepository();
        User user = new User();
        user.setEmail("email@mail.com");
        user.setBirthday(LocalDate.of(2000, 10, 10));

        try {
            userService.createUser(user);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                            " \"логин не может быть пустым и не должен содержать пробелы\"",
                    exp.getMessage());
        }
    }

    @Test
    @DisplayName("Birthday")
    void shouldCreateUserWithWrongBDay() {
        UserRepository userService = new InMemoryUserRepository();
        User user = new User();
        user.setLogin("Login");
        user.setEmail("email@mail.com");
        user.setBirthday(LocalDate.of(2100, 10, 10));

        try {
            userService.createUser(user);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST +
                    " \"дата рождения не может быть в будущем\"", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Update User without Id")
    void shouldUpdateUserWithoutId() {
        UserRepository userService = new InMemoryUserRepository();
        User user = new User();

        try {
            userService.update(user);
        } catch (ResponseStatusException exp) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND +
                    " \"Пользователь с id = " + user.getId() + " не найден\"", exp.getMessage());
        }
    }
}

