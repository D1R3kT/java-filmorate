package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> update(User user);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> findCommonFriends(Long userId, Long friendId);

    void delete(Long id);

    void deleteAll();
}