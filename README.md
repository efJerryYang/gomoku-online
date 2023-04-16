# Gomoku Online

[中文](README_zh.md)

## Introduction

Gomoku Online - HITSZ Distributed Systems Course Project 1 in Spring 2023.

See `docs/` for more details:

- [setup-and-run-the-project](docs/setup-and-run-the-project.md): Detailed setup guide for the project.
- [documentation.md](docs/documentation.md): Documentation for the project.
- [documentation-appendix.md](docs/documentation-appendix.md): Appendix for the documentation (includes demo).
- [workflow.md](docs/workflow.md): Draft design of the workflow.
- [debugging-notes.md](docs/debugging-notes.md): Debugging notes.

## Tech Stack

| Tech stack                                            | Description                                                                                        |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| [Vue.js](https://vuejs.org/)                          | A progressive JavaScript framework used for building user interfaces and single-page applications. |
| [Spring Boot](https://spring.io/projects/spring-boot) | A popular Java-based open-source framework used for building web applications.                     |

<!-- | [Mybatis](https://mybatis.org/mybatis-3/)             | A persistence framework with support for custom SQL, stored procedures and advanced mappings. | -->
<!-- | [MySQL](https://www.mysql.com/)                       | A relational database management system.                                                      | -->

### How to run

Frontend is a `Vue.js` project, the built `dist` directory can be loaded by web server like `nginx` (but there are bugs at the moment when packaging stone images), or you can use `npm` to run it locally as follows:

```sh
cd gomoku-online-frontend/  # Goto frontend working directory
npm install  # Project setup
npm run dev  # Compile and hot-reload for development
```

Backend is a `Spring Boot` project. It is managed by `maven`, so you can run the following to build it manually:

```sh
cd gomoku-online-backend/  # Goto backend working directory
mvn install  # Install necessary dependencies
mvn clean package  # Build an executable jar file for the backend project
```

Alternatively, you can directly run the released `jar` binary in the following way. It is important to notice that you should replace the `<version>` with the exact version of the `jar` file (e.g. `0.1.0`).

```sh
java -jar gomoku-online-<version>.jar # Run the jar file
```
