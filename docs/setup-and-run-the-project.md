# Setup and run the project

## Requirements

> Only tested on Linux

We use `sdkman` to manage the `java` and `maven` version.

- `java 17.0.6-tem`
- `maven >= 3.9.0`

For `node.js` and `npm`, we use `nvm` as the package manager.

- `node >= 18.6.0`
- `npm >= 8.18.0`

## Installation

### For the frontend

Assuming you are at the root directory:

```sh
cd gomoku-online-frontend/
npm install
npm run dev
```

Then, you will have the client hosted at `localhost:8081`.

### For the backend

Assuming you are at the root directory:

```sh
cd gomoku-online-backend/  # Goto backend working directory
mvn install  # Install necessary dependencies
mvn clean package  # Build an executable jar file for the backend project
```

After building the package, you can find the `jar` file in the `target` directory. You can run the following to start the server at `localhost:8080`:

```sh
cd target/
java -jar gomoku-online-<version>.jar # Run the jar file
```
