# Eriantys
![alt text](./resources/assets/backgrounds/eryantis_background.png "Eryantis")

# Server

### Database

For running the server, you need a **PostgreSQL** database.
You can run it on your machine, or with Docker.

#### Mac/Linux

Run the Docker Image with DockerCompose, it'll build the image the first time

```
make up
```

#### Windows
Build the Docker Image
```
docker-compose build
```

Run the Docker Image with DockerCompose
```
docker-compose up
```


------------

### Server

You can start the server with
```
make run-server
```
The Server connects to the database, looking in the .env file in the **root** directory.

The default configuration of the database is:
```
DB_HOST: localhost
DB_NAME: game
DB_USER: user
DB_PASSWORD: password
```


