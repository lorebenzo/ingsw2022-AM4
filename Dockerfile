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
CMD mvn compilev