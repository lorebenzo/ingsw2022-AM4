

CREATE SCHEMA users;

CREATE TABLE IF NOT EXISTS users.users (
    "username" VARCHAR(255) not null unique primary key,
    "password" CHAR(255) not null
);


CREATE TABLE IF NOT EXISTS users.current_games (
    "username" VARCHAR(255) not null,
    "gameUUID" uuid not null,
    "schoolBoardID" int not null,
    "expert" BOOLEAN,
    PRIMARY KEY(username, "gameUUID", "schoolBoardID")
);



CREATE SCHEMA event;

CREATE TABLE IF NOT EXISTS event.aggregate_type (
    "aggregate_name" TEXT NOT NULL,
    "aggregate_id" serial,
    PRIMARY KEY ("aggregate_id")
);

CREATE TABLE IF NOT EXISTS event.aggregates (
    "id" uuid NOT NULL,
    "type" serial,
    PRIMARY KEY ("id"),
    foreign key(type) references event.aggregate_type(aggregate_id)
);

CREATE TABLE IF NOT EXISTS event.events (
    "id" uuid NOT NULL,
    "aggregate_id" uuid not null,
    "event" json not null,
    "event_class" int not null,
    "event_parent_uuid" uuid not null,
    "aggregate_version" int not null,
    PRIMARY KEY ("id"),
    FOREIGN KEY ("aggregate_id") references event.aggregates(id)
);

CREATE TABLE IF NOT EXISTS event.snapshots (
   "aggregate_id" uuid not null,
   "snap" json not null,
   "version" int not null,
   PRIMARY KEY ("aggregate_id", "version"),
    FOREIGN KEY ("aggregate_id") references event.aggregates(id)
);

