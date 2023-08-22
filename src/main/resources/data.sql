DELETE FROM LIKES;
DELETE FROM FILM_GENRES;
DELETE FROM USER_FRIENDS;
DELETE FROM USERS;
DELETE FROM FILM;

ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE FILM ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE USER_FRIENDS ALTER COLUMN USER_FRIENDS_ID RESTART WITH 1;
ALTER TABLE FILM_GENRES ALTER COLUMN FILM_GENRES_ID RESTART WITH 1;
ALTER TABLE LIKES ALTER COLUMN LIKE_ID RESTART WITH 1;

MERGE INTO RATING_MPA KEY(RATING_ID)
    VALUES (1, 'G', 'Нет возрастных ограничений'),
           (2, 'PG', 'Рекомендуется присутствие родителей'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

MERGE INTO GENRE KEY(GENRE_ID)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');