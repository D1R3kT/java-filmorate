package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@JdbcTest
@Import({JdbcUserRepository.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DisplayName("JdbcUserRepository")
public class JdbcUserRepositoryTest {
    public static final long TEST_USER_ID = 1;
    public static final long TEST_FRIEND_ID = 2;
    public static final long TEST_FRIEND_ID2 = 3;
    private final JdbcUserRepository userRepository;


    static User getTestUser() {
        final User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("test1@yandex.ru");
        user.setName("user1");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000, 3, 22));
        return user;
    }

    static Collection<User> getTestFriend() {
        final User user = new User();
        user.setId(TEST_FRIEND_ID2);
        user.setEmail("test3@yandex.ru");
        user.setName("user3");
        user.setLogin("user3");
        user.setBirthday(LocalDate.of(2002, 3, 22));
        Collection<User> users = new ArrayList<>();
        users.add(user);
        return users;
    }


    @Test
    @DisplayName("Должен находить пользователя по id")
    public void should_return_user_when_find_by_id() {
        Optional<User> userOptional = userRepository.findById(TEST_USER_ID);

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("Должен вывести список общих друзей")
    public void should_return_common_friend() {
        Collection<User> friend1 = userRepository.getFriends(TEST_USER_ID);
        Collection<User> friend2 = userRepository.getFriends(TEST_FRIEND_ID);

        Assertions.assertEquals(friend1, friend2);
    }

    @Test
    @DisplayName("Должен найти друзей по id")
    public void should_return_friend_when_find_by_id() {
        Collection<User> friends = userRepository.getFriends(TEST_FRIEND_ID);

        Assertions.assertEquals(friends, getTestFriend());
    }
}
