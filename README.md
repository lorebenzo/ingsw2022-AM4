# Eriantys

# Server

###Database

For running the server, you need a **PostgreSQL** database.
You can run it on your machine, or with Docker.

####Mac/Linux
Build the Docker Image
```
make init
```

Run the Docker Image with DockerCompose

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

The Server connects to the database, looking in the .env file in the **root** directory.

The default configuration of the database is:
```
DB_NAME: game
DB_USER: user
DB_PASSWORD: password
```


