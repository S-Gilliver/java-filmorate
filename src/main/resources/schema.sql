create table FILM
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING(200) not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    Rate int,
    MPA_ID       INTEGER                not null,
    constraint "pk_Film"
        primary key (ID)
);

create table LIKES
(
    LIKE_ID INTEGER auto_increment,
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint PK_LIKE
        primary key (LIKE_ID)
);

create table RATING_MPA
(
    RATING_ID   INTEGER auto_increment,
    NAME        CHARACTER VARYING(10)  not null
        constraint UC_RATING_MPA_NAME
            unique,
    DESCRIPTION CHARACTER VARYING(200) not null,
    constraint PK_RATING_MPA
        primary key (RATING_ID)
);

create table FILM_GENRES
(
    FILM_GENRES_ID INTEGER auto_increment,
    FILM_ID       INTEGER not null,
    GENRE_ID      INTEGER not null,
    constraint PK_FILM_GENRES
        primary key (FILM_GENRES_ID)
);

create table GENRE
(
    GENRE_ID INTEGER auto_increment,
    NAME     CHARACTER VARYING(200) not null
    constraint UC_GENRE_NAME unique,
    constraint PK_GENRE
        primary key (GENRE_ID)
);


create table USERS
(
    ID       INTEGER auto_increment,
    EMAIL    CHARACTER VARYING(200) not null,
    LOGIN    CHARACTER VARYING(50)  not null,
    NAME     CHARACTER VARYING(200) not null,
    BIRTHDAY DATE                   not null,
    constraint PK_USER primary key (ID),
    constraint UC_USER_EMAIL UNIQUE (Email)
);

create table USER_FRIENDS
(
    USER_FRIENDS_ID INTEGER auto_increment,
    USER_ID         INTEGER not null,
    FRIEND_ID       INTEGER not null,
    STATUS          BOOLEAN not null,
    constraint PK_USER_FRIENDS
        primary key (USER_FRIENDS_ID)
);

ALTER TABLE Film ADD CONSTRAINT IF NOT EXISTS fk_Film_Rating_ID FOREIGN KEY(MPA_ID)
    REFERENCES RATING_MPA (RATING_ID) ON DELETE RESTRICT;

ALTER TABLE Likes ADD CONSTRAINT IF NOT EXISTS fk_Like_User_ID FOREIGN KEY(USER_ID)
    REFERENCES Users (ID);

ALTER TABLE Likes ADD CONSTRAINT IF NOT EXISTS fk_Like_Film_ID FOREIGN KEY(FILM_ID)
    REFERENCES Film (ID) ON DELETE CASCADE;

ALTER TABLE USER_FRIENDS ADD CONSTRAINT IF NOT EXISTS fk_USER_FRIENDS_USER_ID FOREIGN KEY(USER_ID)
    REFERENCES Users (ID);

ALTER TABLE USER_FRIENDS ADD CONSTRAINT IF NOT EXISTS fk_USER_FRIENDS_FRIEND_ID FOREIGN KEY(FRIEND_ID)
    REFERENCES Users (ID);

ALTER TABLE FILM_GENRES ADD CONSTRAINT IF NOT EXISTS fk_FILM_GENRES_FILM_ID FOREIGN KEY(FILM_ID)
    REFERENCES Film (ID) ON DELETE CASCADE;

ALTER TABLE FILM_GENRES ADD CONSTRAINT IF NOT EXISTS fk_FILM_GENRES_GENRE_ID FOREIGN KEY(GENRE_ID)
    REFERENCES Genre (GENRE_ID) ON DELETE RESTRICT;

ALTER TABLE FILM_GENRES ADD CONSTRAINT IF NOT EXISTS UC_FILM_GENRES_Genre_ID_Film_ID UNIQUE (GENRE_ID, FILM_ID)