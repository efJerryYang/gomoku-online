# Workflow

## Overview

1. Requirements analysis and planning
2. Logical design
3. Frontend development
4. Backend development
5. Testing and debugging
6. Deployment and maintenance

## Details

### Requirements analysis and planning

1. Goal: The main goal is to support multiple-paris online Gomoku game. Data persistence is optional.
2. User requirements:
   1. Each game is for two players.
   2. The game alternates between two players. A stone is placed on the board by each player in turn.
   3. A player wins if he/she gets five stones in a row, column or diagonal.
   4. (Optional) Play with AI.
   5. Username should be temporary.
3. Technical requirements:
   1. Frontend: Vue.js
   2. Backend: Spring Boot
   3. (Optional) Database: MySQL
   4. (Optional) AI: Alpha-Beta pruning/Monte Carlo tree search
4. Project timeline:
   1. The project should be finished in 2 weeks.
   2. The docs should be done today.

### Logical design

1. Frontend
   1. Pages:
      1. Home page
         1. Input box: input username
         2. Button `pick`: submit input username
         3. Message box: show notification
         4. Button `dice` (at the schema row of table): match with a random player
         5. Table box: show matching list
            1. Schema: `[{ waiting_time: number, username: string (, id: number) }]`
            2. Matching list is sorted by `waiting_time` in descending order.
            3. Button `match`: match with the player
            4. Scroll bar: scroll the list
            5. The list is refreshed automatically every 5 seconds.
         6. Game block: 1. Text `you`: show your username 2. Text `opponent`: show opponent's username 3. Bold the name if it's the player's turn 4. Count down timer: show remaining time (max 30s) 5. Board: 15x15 grid 1. Click to place a stone 2. Background of cells are the same image placed together 3. Result panel: show result 1. Text field `result`: show result `You: 0, Opponent: 0` 2. Button `start new round`: restart the game (clear board) 3. Button `exit`: exit current game (clear opponent, refresh result panel, clear board)
         <!-- 4. (Optional)Button `history`: goto history page -->
         <!-- 2. (Optional) History page -->
2. Backend
   1. API:
      1. `GET /api/match`: get matching list
      2. `POST /api/match`: match with a player
      3. `POST /api/match/dice`: match with a random player
      4. `GET /api/game`: get game info
      5. `POST /api/game`: place a stone
      6. `POST /api/game/exit`: exit current game
      7. (Optional) `GET /api/history`: get history list
      8. (Optional) `GET /api/history/:id`: get history info
   2. Data structure:
      1. `Match`: `[{ waiting_time: number, username: string (, id: number) }]`
      2. `Game`: `{ id: number, player1: string, player2: string, turn: number, board: number[][], result: number }`
      3. `History`: `[{ id: number, player1: string, player2: string, result: number }]`
      4. `HistoryInfo`: `{ id: number, player1: string, player2: string, board: number[][], result: number }`
   3. Data persistence:
      1. (Optional) MySQL
      2. (Optional) Redis
   4. AI:
      1. (Optional) Alpha-Beta pruning
      2. (Optional) Monte Carlo tree search
