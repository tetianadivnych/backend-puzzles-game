FROM openjdk:11-jdk-oracle
ADD . /backend-puzzles-game
WORKDIR /backend-puzzles-game
CMD ["java", "-jar", "./target/*.jar"]
