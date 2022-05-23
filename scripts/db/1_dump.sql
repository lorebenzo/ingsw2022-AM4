

CREATE SCHEMA users;

CREATE TABLE IF NOT EXISTS users.users (
    "username" VARCHAR(255) not null unique primary key,
    "password" CHAR(255) not null
);