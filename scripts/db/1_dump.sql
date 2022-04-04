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


