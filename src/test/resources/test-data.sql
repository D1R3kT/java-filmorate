INSERT INTO users (email, login, user_name, birthday)
VALUES ('test1@yandex.ru', 'user1', 'user1', '2000-03-22'),
       ('test2@yandex.ru', 'user2', 'user2', '2001-03-22'),
       ('test3@yandex.ru', 'user3', 'user3', '2002-03-22');

INSERT INTO friends(user_id, friend_id)
VALUES (1, 3),
       (2, 3);
