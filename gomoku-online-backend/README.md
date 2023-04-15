# Gomoku Oneline

This is the backend part of the project.

Backend is a `Spring Boot` project. It is managed by `maven`, so you can run the following to build it manually:

```sh
mvn install  # Install necessary dependencies
mvn clean package  # Build an executable jar file for the backend project
```

Alternatively, you can directly run the released `jar` binary in the following way. It is important to notice that you
should replace the `<version>` with the exact version of the `jar` file (e.g. `0.1.0`).

```sh
java -jar gomoku-online-<version>.jar # Run the jar file
```
