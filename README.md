# Description
This is the Java backend part of a project which generates puzzles and enables users to assemble them manually. 
The application can also check if the assembled puzzles match an original picture. 

When this server is up, launch [the frontend](https://github.com/tetianadivnych/frontend-puzzles-game/tree/main).

## Prerequisites
Java JDK version 11 should be installed in the system.

You can download Java from here https://www.oracle.com/java/technologies/downloads/#java11
or here https://adoptium.net/temurin/releases/

## How to Build
  
To build the application execute the following commands in the project folder (where pom.xml and mvnw are located). Before building, set permissions on the ./mvnw file in the cloned project directory:
```
chmod 755 ./mvnw
```{{exec}}

```bash
./mvnw clean package # this will build the project
```
For the first time, it will download and install the Maven version configured in the project settings (`v.3.8.1`). Next time the cached version will be used without re-downloading.

After the build is completed, the folder `/target` will be created with a compiled `.jar` ready to be launched.

## How to Run
You can launch the server for example at port `8080`
(if the option `--server.port=8080` is not provided the default port is `8080`):
```bash
java -jar ./target/*.jar --server.port=8080
```
You might need to replace * with the actual `.jar` name, for example:
```bash
java -jar ./target/puzzles-game-0.0.1-SNAPSHOT.jar --server.port=8080
```

## How to Run with Docker
To containerize the application, make sure you have installed Docker and started the Docker daemon on your computer, as well as have built the application manually.
1. Build the container image from the project root directory. 
```
docker build -t backend-puzzles-game .
```
2. Start your container.
```
docker run -p 8080:8080 backend-puzzles-game
```
Detailed instruction on Docker setup is available [here](https://docs.docker.com/get-started/02_our_app/).

## How to access CI Pipeline 
[![java-maven](https://github.com/tetianadivnych/backend-puzzles-game/actions/workflows/maven.yml/badge.svg)](https://github.com/tetianadivnych/backend-puzzles-game/actions/workflows/maven.yml)

The project contains a maven.yml file to enable continuous integration. Click the badge above to see project build workflow statuses.
