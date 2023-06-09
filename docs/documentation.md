# Project Documentation

## Overview

The documentation is for HITSZ Distributed Systems Project 1 in Spring 2023. My name is Yang Jierui and the student ID is 200110428. You can find the demo at the URL [http://8.130.68.222:8081](http://8.130.68.222:8081).

The purpose of this documentation is to provide a comprehensive explanation of the design and implementation of the multiplayer Gomoku Online game, which allows multiple players to interact in real-time. The game can be accessed via the demo URL located [here](http://8.130.68.222:8081), and it is made up of a client application that was implemented using the `Vue.js` framework and a server application based on `Spring Boot`. You can also find the project at my GitHub repository [here](https://github.com/efJerryYang/gomoku-online), which provides additional installation details. The GitHub repository will not be made public until the assignment due date is reached.

## System Structure

The system is structured into two components, namely the client-side and the server-side. The client-side is responsible for rendering the game's user interface, which includes the player waiting list and the game board for Gomoku, and handling user's events like clicking buttons or submitting inputs. The server-side handles communication between multiple clients and manages the game's state, as well as the moves list for potential further needs for game history. The server side also manages clients' identification utilizing the `JWT` verification process.

## Client-Server Communication

The client and server communication relies on `HTTP` protocol. The client continuously polls for updates from the server to refresh the players waiting list and the matching status. Upon the first connection, the client will be assigned a `clientId` automatically, which represents a unique identifier for the specific client. To join the matching list, the player needs to `pick` a username. Once a match is submitted by the player via clicking `match` button, the client will send a `POST` request to the server indicating that it is going to start a game. A player can match with any other player by clicking the `match` button at the matching list, or can `dice` to obtain a random player. The newly created game will be set to `PENDING` status, and the server will update the game until the other matched player's polling for matching status reaches the server, and the game will be updated to `PLAYING`. Once a player wins the game, the game status will be updated to `PLAYERX_WIN`(`X` can be `1` or `2`). If a tie status is reached, the game will be updated to `TIE`. All of the three status marks an end to a game, and players are not allowed to move on the board of already-ended games. A player can either start a new game or exit the game by clicking the buttons displayed under the board.

## Detailed Explanation of Design

### Frontend

The Gomoku online game is powered by a Vue app with a `App.vue` main component. The template consists of a home section and a game section that are meticulously designed to offer users a seamless and engaging experience. In the home section, players can enter their username and match with either a random or a specific player. The game section, on the other hand, showcases a `BOARD_SIZE` x `BOARD_SIZE` board where players can strategically place their stones, with a result panel displaying score and notifications alongside a status segment featuring player and opponent names and turn status. The app provides an exciting and dynamic gaming experience that offers players an avenue for relishing every moment.

The Vue app script section offers comprehensive game functionalities that cater to the user's gaming preferences. With the ability to enter their username, users can match with other players either randomly or by choice, marking the inauguration of a two-player game. Players make alternating moves by placing stones on the board, initiating interface updates, and scoreboard displays of their scores. The script employs axios for `HTTP` requests to match players and make moves, with a user's authentication token stored in local storage to enhance subsequent requests. The app also features an error update notification function that triggers if an `HTTP` request error occurs, alongside a functional aspect that allows players to initiate a new round of the game.

### Backend

This backend serves as the server-side logic for a web application, and it is built on the `Spring Boot` framework. The backend provides RESTful APIs for a Gomoku online game, allowing players to match with each other, start and play games, and communicate with each other during gameplay.

The backend consists of several components, including `controllers`, `services`, `entities`, `data transfer objects` (DTOs), and `utility` classes. The controllers handle incoming requests and delegate to the corresponding services, which implement the business logic for the game. The entities represent the persistent data model for the game, including players, games, and moves. The DTOs serve as data transfer objects between the client and the server, and are used to serialize and deserialize `JSON` data.

In addition, the backend has implemented security using `Spring Security`, and uses `JSON Web Tokens (JWTs)` for authentication and authorization. The `CryptoUtil` class provides utility methods for generating a secret key for possible usage during `JWT` process.

You can see the backend project structure below:

```txt
src/
|-- main/
|   |-- java/
|   |   `-- me/
|   |       `-- efjerryyang/
|   |           `-- gomokuonline/
|   |               |-- controller/
|   |               |-- dto/
|   |               |-- entity/
|   |               |-- service/
|   |               |-- util/
|   |               |-- Constant.java
|   |               |-- GomokuOnlineApplication.java
|   |               |-- CorsConfiguration.java
|   |               `-- SecurityConfiguration.java
|   `-- resources
|       |-- application.yaml
|       |-- static
|       `-- templates
`-- test
    `-- java
        `-- me
            `-- efjerryyang
                `-- gomokuonline
                    `-- GomokuOnlineApplicationTests.java
```

## Support for Multiple Players

The game's support for multiple players is enabled by allowing multiple clients to connect to the server. A unique `jwtToken` set by previously generated `clientId` distinguishes different clients and a `JwtService` handles the verification process of clients. The server maintains a list for all users and updates their game state accordingly and it ensures that one client can only control a player at any given time.

## Features

Other key features added to the Gomoku Online game for better user experience include:

- **Instant Placement**: stones are placed on the board instantly if the move is valid
- **Validation**: detection and rejection of invalid moves on both client and server side
- **New Round**: enables players to start a new game after reaching the end of a previous one
- **Exit Game**: users are free to exit the game at any time
- **Logging**: detailed logs are generated by the server
- **Customization**: board size can be changed from the server side

## Conclusion

In conclusion, this documentation has highlighted the structure, design, and implementation of the multiplayer gomoku online game. The frontend is based on a `Vue.js` framework, and the backend is built on `Spring Boot`. This game provides a seamless and engaging UI and offers an exciting and dynamic gaming experience to players. The game's support for multiple players is enabled by allowing multiple clients to connect to the server. A unique client identification system is used to enable verification and control player access. Overall, this game provides a compelling gaming experience that allows multiple players to interact in real-time.

## Appendix

See file `documentation-appendix` for more details.
