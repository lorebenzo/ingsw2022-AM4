#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY pom.xml /usr/src/ing-sw/
CMD mvn -f /usr/src/ing-sw/pom.xml clean package

FROM build as test
CMD mvn -f /usr/src/ing-sw/pom.xml clean test

FROM build as run
CMD mvn compilev

FROM postgres:14.1 as db
ADD /scripts/db/1_dump.sql /docker-entrypoint-initdb.d
#ADD /scripts/db/2_data.sql /docker-entrypoint-initdb.d

RUN chmod a+r /docker-entrypoint-initdb.d/*