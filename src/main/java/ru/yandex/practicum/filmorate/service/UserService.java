package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Component
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final Map<Long, Set<Long>> friends = new HashMap<>();

    //  добавление в друзья
    public void addFriend(Long userId, Long friendId) {
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + " не найден");
        } else if (userRepository.getUser(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + friendId + " не найден");
        }
        Set<Long> uFriendsId = friends.computeIfAbsent(userId, id -> new HashSet<>());
        uFriendsId.add(friendId);
        friends.put(userId, uFriendsId);

        Set<Long> fFriendsId = friends.computeIfAbsent(friendId, id -> new HashSet<>());
        fFriendsId.add(userId);
        friends.put(friendId, fFriendsId);
    }

    //  вывод списка друзей пользователя
    public Set<Long> getAllFriend(Long userId) {
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + "не найден");
        }
        Set<Long> uFriendsId = friends.computeIfAbsent(userId, id -> new HashSet<>());
        return uFriendsId;
    }

    //  удаление из друзей
    public void removeFriend(Long userId, Long friendId) {
        if (userRepository.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + userId + " не найден");
        }
        if (userRepository.getUser(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "пользователь с id = " + friendId + " не найден");
        }
        Set<Long> uFriendsId = friends.computeIfAbsent(userId, id -> new HashSet<>());
        uFriendsId.remove(friendId);
        friends.put(userId, uFriendsId);

        Set<Long> fFriendsId = friends.computeIfAbsent(friendId, id -> new HashSet<>());
        fFriendsId.remove(userId);
        friends.put(friendId, fFriendsId);
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        Set<Long> uFriendsId = friends.get(userId);
        Set<Long> fFriendsId = friends.get(friendId);

        Set<User> commonFriends = new HashSet<>();
        for (Long id : fFriendsId) {
            if (uFriendsId.contains(id)) {
                commonFriends.add(userRepository.getUser(id));
            }
        }
        return commonFriends;
    }


}
