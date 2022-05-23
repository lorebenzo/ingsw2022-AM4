

CREATE SCHEMA users;

CREATE TABLE IF NOT EXISTS users.users (
    "username" VARCHAR(255) not null unique primary key,
    "password" CHAR(255) not null
);

CREATE TABLE IF NOT EXISTS users.user_game (
   "username" VARCHAR(255) not null primary key,
   "gameUUID" uuid not null primary key,
   "schoolBoardID" int not null primary key
);