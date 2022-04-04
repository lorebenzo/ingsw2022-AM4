#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM build as test
CMD mvn -f /home/app/pom.xml clean test

FROM build as run
CMD mvn compile

FROM postgres:14.1 as db
ADD /scripts/db/1_dump.sql /docker-entrypoint-initdb.d
ADD /scripts/db/2_data.sql /docker-entrypoint-initdb.d

RUN chmod a+r /docker-entrypoint-initdb.d/*