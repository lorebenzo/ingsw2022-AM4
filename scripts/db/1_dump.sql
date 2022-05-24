

CREATE SCHEMA users;

CREATE TABLE IF NOT EXISTS users.users (
    "username" VARCHAR(255) not null unique primary key,
    "password" CHAR(255) not null
);

CREATE TABLE IF NOT EXISTS users.user_game (
   "username" VARCHAR(255) not null,
   "gameUUID" uuid not null,
   "schoolBoardID" int not null,
    PRIMARY KEY(username, "gameUUID", "schoolBoardID")
);