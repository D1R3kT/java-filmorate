package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.excepion.NotFoundException;
import ru.yandex.practicum.filmorate.excepion.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {

    @Test
    @DisplayName("Name")
    void shouldCreateUserWithoutName() {
        UserController userController = new UserController();
        User user = new User();
        user.setEmail("email@mail.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2000,10,10));
        userController.createUser(user);

        Assertions.assertEquals("Login", user.getName());
    }

    @Test
    @DisplayName("Email")
    void shouldCreateUserWithWrongEmail() {
        UserController userController = new UserController();
        User user = new User();
        user.setEmail("emailmail.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2000, 10, 10));

        try {
            userController.createUser(user);
        } catch (ValidationException exp) {
            Assertions.assertEquals("электронная почта не может быть пустой и должна содержать символ @",
                    exp.getMessage());
        }
    }

    @Test
    @DisplayName("Login")
    void shouldCreateUserWithoutLogin() {
        UserController userController = new UserController();
        User user = new User();
        user.setEmail("email@mail.com");
        user.setBirthday(LocalDate.of(2000,10,10));

        try {
            userController.createUser(user);
        } catch (ValidationException exp) {
            Assertions.assertEquals("логин не может быть пустым и не должен содержать пробелы",
                    exp.getMessage());
        }
    }

    @Test
    @DisplayName("Birthday")
    void shouldCreateUserWithWrongBDay() {
        UserController userController = new UserController();
        User user = new User();
        user.setLogin("Login");
        user.setEmail("email@mail.com");
        user.setBirthday(LocalDate.of(2100,10,10));

        try {
            userController.createUser(user);
        } catch (ValidationException exp) {
            Assertions.assertEquals("дата рождения не может быть в будущем", exp.getMessage());
        }
    }

    @Test
    @DisplayName("Update User without Id")
    void shouldUpdateUserWithoutId() {
        UserController userController = new UserController();
        User user = new User();

        try {
            userController.update(user);
        } catch (NotFoundException exp) {
            Assertions.assertEquals("Пользователь с id = " + user.getId() + " не найден", exp.getMessage());
        }
    }








}

