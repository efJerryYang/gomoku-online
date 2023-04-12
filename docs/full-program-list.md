# Program List

## For running backend program

> Only test on Linux.

- java 17.0.0-tem
- maven
- node.js, npm, vue.js

## How to run the project

### For the frontend

Assuming you are at the root directory:

```sh
cd gomoku-online-frontend/
npm install
npm run dev
```

Then, you will have the client hosted on the port `8081`.

### For the backend

Assuming you are at the root directory:

```sh
cd gomoku-online-backend/  # Goto backend working directory
mvn install  # Install necessary dependencies
mvn clean package  # Build an executable jar file for the backend project
```

After building the package, you can find the `jar` file in the `target` directory. You can run the following to run the server:

```sh
cd target/
java -jar gomoku-online-<version>.jar # Run the jar file
```
